package edu.asu.spring.quadriga.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ram Kumar Kumaresan
 */
@Entity
@Table(name = "tbl_quadriga_user_denied")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "QuadrigaUserDeniedDTO.findAll", query = "SELECT q FROM QuadrigaUserDeniedDTO q"),
    @NamedQuery(name = "QuadrigaUserDeniedDTO.findByFullname", query = "SELECT q FROM QuadrigaUserDeniedDTO q WHERE q.fullname = :fullname"),
    @NamedQuery(name = "QuadrigaUserDeniedDTO.findByUsername", query = "SELECT q FROM QuadrigaUserDeniedDTO q WHERE q.username = :username"),
    @NamedQuery(name = "QuadrigaUserDeniedDTO.findByPasswd", query = "SELECT q FROM QuadrigaUserDeniedDTO q WHERE q.passwd = :passwd"),
    @NamedQuery(name = "QuadrigaUserDeniedDTO.findByEmail", query = "SELECT q FROM QuadrigaUserDeniedDTO q WHERE q.email = :email"),
    @NamedQuery(name = "QuadrigaUserDeniedDTO.findByDeniedby", query = "SELECT q FROM QuadrigaUserDeniedDTO q WHERE q.deniedby = :deniedby"),
    @NamedQuery(name = "QuadrigaUserDeniedDTO.findByUpdatedby", query = "SELECT q FROM QuadrigaUserDeniedDTO q WHERE q.updatedby = :updatedby"),
    @NamedQuery(name = "QuadrigaUserDeniedDTO.findByUpdateddate", query = "SELECT q FROM QuadrigaUserDeniedDTO q WHERE q.updateddate = :updateddate"),
    @NamedQuery(name = "QuadrigaUserDeniedDTO.findByCreatedby", query = "SELECT q FROM QuadrigaUserDeniedDTO q WHERE q.createdby = :createdby"),
    @NamedQuery(name = "QuadrigaUserDeniedDTO.findByCreateddate", query = "SELECT q FROM QuadrigaUserDeniedDTO q WHERE q.createddate = :createddate")})
public class QuadrigaUserDeniedDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "actionid")
    private String actionid;
    @Column(name = "fullname")
    private String fullname;
    @Basic(optional = false)
    @Column(name = "username")
    private String username;
    @Column(name = "passwd")
    private String passwd;
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @Column(name = "deniedby")
    private String deniedby;
    @Basic(optional = false)
    @Column(name = "updatedby")
    private String updatedby;
    @Basic(optional = false)
    @Column(name = "updateddate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateddate;
    @Basic(optional = false)
    @Column(name = "createdby")
    private String createdby;
    @Basic(optional = false)
    @Column(name = "createddate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createddate;

	public QuadrigaUserDeniedDTO() {
    }

    public QuadrigaUserDeniedDTO(String username) {
        this.username = username;
    }

    public QuadrigaUserDeniedDTO(String username, String quadrigarole, String updatedby, Date updateddate, String createdby, Date createddate) {
        this.username = username;
        this.setDeniedby(quadrigarole);
        this.updatedby = updatedby;
        this.updateddate = updateddate;
        this.createdby = createdby;
        this.createddate = createddate;
    }
    
    public String getActionid() {
		return actionid;
	}

	public void setActionid(String actionid) {
		this.actionid = actionid;
	}

    public String getDeniedby() {
		return deniedby;
	}

	public void setDeniedby(String deniedby) {
		this.deniedby = deniedby;
	}

	public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getUpdatedby() {
        return updatedby;
    }

    public void setUpdatedby(String updatedby) {
        this.updatedby = updatedby;
    }

    public Date getUpdateddate() {
        return updateddate;
    }

    public void setUpdateddate(Date updateddate) {
        this.updateddate = updateddate;
    }

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    public Date getCreateddate() {
        return createddate;
    }

    public void setCreateddate(Date createddate) {
        this.createddate = createddate;
    }

 
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (username != null ? username.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof QuadrigaUserDeniedDTO)) {
            return false;
        }
        QuadrigaUserDeniedDTO other = (QuadrigaUserDeniedDTO) object;
        if ((this.username == null && other.username != null) || (this.username != null && !this.username.equals(other.username))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "hpsdtogeneration.QuadrigaUserDTO[ username=" + username + " ]";
    }
    
}
