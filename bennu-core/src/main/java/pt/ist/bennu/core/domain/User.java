/* 
* @(#)User.java 
* 
* Copyright 2009 Instituto Superior Tecnico 
* Founding Authors: João Figueiredo, Luis Cruz, Paulo Abrantes, Susana Fernandes 
*  
*      https://fenix-ashes.ist.utl.pt/ 
*  
*   This file is part of the Bennu Web Application Infrastructure. 
* 
*   The Bennu Web Application Infrastructure is free software: you can 
*   redistribute it and/or modify it under the terms of the GNU Lesser General 
*   Public License as published by the Free Software Foundation, either version  
*   3 of the License, or (at your option) any later version. 
* 
*   Bennu is distributed in the hope that it will be useful, 
*   but WITHOUT ANY WARRANTY; without even the implied warranty of 
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
*   GNU Lesser General Public License for more details. 
* 
*   You should have received a copy of the GNU Lesser General Public License 
*   along with Bennu. If not, see <http://www.gnu.org/licenses/>. 
*  
*/
package pt.ist.bennu.core.domain;

import java.util.Comparator;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import pt.ist.bennu.core.domain.exceptions.DomainException;
import pt.ist.bennu.core.domain.groups.IRoleEnum;
import pt.ist.bennu.core.domain.groups.People;
import pt.ist.bennu.core.domain.groups.Role;
import pt.ist.fenixWebFramework.services.Service;

/**
 * 
 * @author João Antunes
 * @author Anil Kassamali
 * @author Paulo Abrantes
 * @author Luis Cruz
 * @author Susana Fernandes
 * 
 */
public class User extends User_Base {

	public interface UserPresentationStrategy {
		public String present(User user);

		public String shortPresent(User user);
	}

	public static final UserPresentationStrategy defaultStrategy = new UserPresentationStrategy() {

		@Override
		public String present(User user) {
			return user.getUsername();
		}

		@Override
		public String shortPresent(User user) {
			return user.getUsername();
		}

	};

	public static final Comparator<User> COMPARATOR_BY_NAME = new Comparator<User>() {

		@Override
		public int compare(final User user1, final User user2) {
			return user1.getUsername().compareTo(user2.getUsername());
		}

	};

	private static UserPresentationStrategy strategy = defaultStrategy;

	public User(final String username) {
		super();
		setUsername(username);
		setMyOrg(MyOrg.getInstance());
	}

	public static User findByUsername(final String username) {
		for (final User user : MyOrg.getInstance().getUserSet()) {
			if (user.getUsername().equalsIgnoreCase(username)) {
				return user;
			}
		}
		return null;
	}

	public boolean hasRoleType(final RoleType roleType) {
		for (final People people : getPeopleGroupsSet()) {
			if (people instanceof Role) {
				final Role role = (Role) people;
				if (role.isRole(roleType) && role.isMember(this)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasRoleType(final String roleAsString) {
		for (final People people : getPeopleGroupsSet()) {
			if (people instanceof Role) {
				final Role role = (Role) people;
				if (role.getGroupName().equals(roleAsString) && role.isMember(this)) {
					return true;
				}
			}
		}
		return false;
	}

	@Service
	@Override
	public void setLastLogoutDateTime(final DateTime lastLogoutDateTime) {
		super.setLastLogoutDateTime(lastLogoutDateTime);
	}

	@Service
	public void generatePassword() {
		final String password = RandomStringUtils.randomAlphanumeric(10);
		setPassword(password);
	}

	@Override
	public void setPassword(final String password) {
		final String newHash = DigestUtils.sha512Hex(password);
		final String oldHash = getPassword();
		if (newHash.equals(oldHash)) {
			throw new DomainException("message.error.bad.old.password",
					DomainException.getResourceFor("resources.MyorgResources"));
		}
		super.setPassword(newHash);
	}

	public boolean matchesPassword(final String password) {
		final String hash = DigestUtils.sha512Hex(password);
		return hash.equals(getPassword());
	}

	@Service
	public static User createNewUser(final String username) {
		return new User(username);
	}

	@Service
	public void addRoleType(final IRoleEnum roleType) {
		final Role role = Role.getRole(roleType);
		addPeopleGroups(role);
	}

	@Service
	@Override
	public void removePeopleGroups(final People peopleGroups) {
		super.removePeopleGroups(peopleGroups);
	}

	public String getPresentationName() {
		return strategy.present(this);
	}

	public String getShortPresentationName() {
		return strategy.shortPresent(this);
	}

	@Override
	public String toString() {
		return getUsername();
	}

	public static void registerUserPresentationStrategy(UserPresentationStrategy newStrategy) {
		if (strategy != defaultStrategy) {
			Logger.getLogger(User.class).warn("Overriding non-default strategy");
		}
		strategy = newStrategy;
	}

	public void delete() {
		removeMyOrg();
		deleteDomainObject();
	}

	public PasswordRecoveryRequest createNewPasswordRecoveryRequest() {
		super.setPassword(null);
		return new PasswordRecoveryRequest(this);
	}

}
