package org.agf.model;

import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Version;
import java.util.Set;
import java.util.HashSet;
import org.agf.model.Task;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import org.agf.model.Goal;

@Entity
public class User implements Serializable {

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

	@Column(nullable = false)
	private String email;

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
	private Set<Task> ownedTasks = new HashSet<Task>();

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
	private Set<Goal> ownedGoals = new HashSet<Goal>();

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
		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (name != null && !name.trim().isEmpty())
			result += "name: " + name;
		if (email != null && !email.trim().isEmpty())
			result += ", email: " + email;
		return result;
	}

	public Set<Task> getOwnedTasks() {
		return this.ownedTasks;
	}

	public void setOwnedTasks(final Set<Task> ownedTasks) {
		this.ownedTasks = ownedTasks;
	}

	public Set<Goal> getOwnedGoals() {
		return this.ownedGoals;
	}

	public void setOwnedGoals(final Set<Goal> ownedGoals) {
		this.ownedGoals = ownedGoals;
	}

}