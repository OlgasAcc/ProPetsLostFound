package proPets.lostFound.model.accessCode;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = { "accCode" })
@ToString
@Entity
@Table(name = "access_codes")
@Access(value = AccessType.FIELD)


public class AccessCode implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	String accCode;


	
	
	/*@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AccessCode)) {
			return false;
		}
		AccessCode other = (AccessCode) obj;
		if (accCode == null) {
			if (other.accCode != null) {
				return false;
			}
		} else if (!accCode.equals(other.accCode)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accCode == null) ? 0 : accCode.hashCode());
		return result;
	}*/
	
}
