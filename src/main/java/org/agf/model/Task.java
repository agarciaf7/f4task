package org.agf.model;

import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Version;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import org.agf.model.Goal;
import javax.persistence.ManyToMany;
import org.agf.model.User;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;

@Entity
public class Task implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;

	@Column(nullable = false)
	private String name;

	@Column
	private boolean due;

	@Column
	private boolean persistent;

	@Column
	private Long priority;

	@Column(nullable = false)
	private Date creation;

	@Column
	private Date deadline;

	@Column
	private String observaciones;

	@ManyToMany
	private Set<Goal> goals = new HashSet<Goal>();

	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,
			CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	private User owner;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Task)) {
			return false;
		}
		Task other = (Task) obj;
		if (id != null) {
			if (!id.equals(other.id)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDue() {
		return due;
	}

	public void setDue(boolean due) {
		this.due = due;
	}

	public boolean isPersistent() {
		return persistent;
	}

	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	public Long getPriority() {
		return priority;
	}

	public void setPriority(Long priority) {
		this.priority = priority;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (name != null && !name.trim().isEmpty())
			result += "name: " + name;
		result += ", due: " + due;
		result += ", persistent: " + persistent;
		if (priority != null)
			result += ", priority: " + priority;
		if (observaciones != null && !observaciones.trim().isEmpty())
			result += ", observaciones: " + observaciones;
		return result;
	}

	public Set<Goal> getGoals() {
		return this.goals;
	}

	public void setGoals(final Set<Goal> goals) {
		this.goals = goals;
	}

	public User getOwner() {
		return this.owner;
	}

	public void setOwner(final User owner) {
		this.owner = owner;
	}

}