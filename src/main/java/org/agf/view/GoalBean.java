package org.agf.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.agf.model.Goal;
import java.util.Iterator;
import org.agf.model.Task;
import org.agf.model.User;

/**
 * Backing bean for Goal entities.
 * <p/>
 * This class provides CRUD functionality for all Goal entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class GoalBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Goal entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Goal goal;

	public Goal getGoal() {
		return this.goal;
	}

	public void setGoal(Goal goal) {
		this.goal = goal;
	}

	@Inject
	private Conversation conversation;

	@PersistenceContext(unitName = "f4task-persistence-unit", type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	public String create() {

		this.conversation.begin();
		this.conversation.setTimeout(1800000L);
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.conversation.isTransient()) {
			this.conversation.begin();
			this.conversation.setTimeout(1800000L);
		}

		if (this.id == null) {
			this.goal = this.example;
		} else {
			this.goal = findById(getId());
		}
	}

	public Goal findById(Long id) {

		return this.entityManager.find(Goal.class, id);
	}

	/*
	 * Support updating and deleting Goal entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.goal);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.goal);
				return "view?faces-redirect=true&id=" + this.goal.getId();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public String delete() {
		this.conversation.end();

		try {
			Goal deletableEntity = findById(getId());
			Iterator<Task> iterTasks = deletableEntity.getTasks().iterator();
			for (; iterTasks.hasNext();) {
				Task nextInTasks = iterTasks.next();
				nextInTasks.getGoals().remove(deletableEntity);
				iterTasks.remove();
				this.entityManager.merge(nextInTasks);
			}
			User owner = deletableEntity.getOwner();
			owner.getOwnedGoals().remove(deletableEntity);
			deletableEntity.setOwner(null);
			this.entityManager.merge(owner);
			this.entityManager.remove(deletableEntity);
			this.entityManager.flush();
			return "search?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching Goal entities with pagination
	 */

	private int page;
	private long count;
	private List<Goal> pageItems;

	private Goal example = new Goal();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Goal getExample() {
		return this.example;
	}

	public void setExample(Goal example) {
		this.example = example;
	}

	public String search() {
		this.page = 0;
		return null;
	}

	public void paginate() {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Goal> root = countCriteria.from(Goal.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Goal> criteria = builder.createQuery(Goal.class);
		root = criteria.from(Goal.class);
		TypedQuery<Goal> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Goal> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String name = this.example.getName();
		if (name != null && !"".equals(name)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("name")),
					'%' + name.toLowerCase() + '%'));
		}
		User owner = this.example.getOwner();
		if (owner != null) {
			predicatesList.add(builder.equal(root.get("owner"), owner));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Goal> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Goal entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Goal> getAll() {

		CriteriaQuery<Goal> criteria = this.entityManager.getCriteriaBuilder()
				.createQuery(Goal.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Goal.class))).getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final GoalBean ejbProxy = this.sessionContext
				.getBusinessObject(GoalBean.class);

		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context,
					UIComponent component, String value) {

				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context,
					UIComponent component, Object value) {

				if (value == null) {
					return "";
				}

				return String.valueOf(((Goal) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Goal add = new Goal();

	public Goal getAdd() {
		return this.add;
	}

	public Goal getAdded() {
		Goal added = this.add;
		this.add = new Goal();
		return added;
	}
}
