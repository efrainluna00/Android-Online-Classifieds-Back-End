/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshop.maven2.jpa.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Efraín
 */
@Entity
@Table(name = "ad", catalog = "eshop", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ad.findAll", query = "SELECT a FROM Ad a"),
    @NamedQuery(name = "Ad.findById", query = "SELECT a FROM Ad a WHERE a.id = :id"),
    @NamedQuery(name = "Ad.findByStringThumbnail", query = "SELECT a FROM Ad a WHERE a.stringThumbnail = :stringThumbnail"),
    @NamedQuery(name = "Ad.findByStringImage", query = "SELECT a FROM Ad a WHERE a.stringImage = :stringImage"),
    @NamedQuery(name = "Ad.findByName", query = "SELECT a FROM Ad a WHERE a.name = :name"),
    @NamedQuery(name = "Ad.findByDescription", query = "SELECT a FROM Ad a WHERE a.description = :description"),
    @NamedQuery(name = "Ad.findByCost", query = "SELECT a FROM Ad a WHERE a.cost = :cost"),
    @NamedQuery(name = "Ad.findByStatus", query = "SELECT a FROM Ad a WHERE a.status = :status"),
    @NamedQuery(name = "Ad.findByPostingDate", query = "SELECT a FROM Ad a WHERE a.postingDate = :postingDate"),
    @NamedQuery(name = "Ad.findByViews", query = "SELECT a FROM Ad a WHERE a.views = :views"),
    @NamedQuery(name = "Ad.findByLati", query = "SELECT a FROM Ad a WHERE a.lati = :lati"),
    @NamedQuery(name = "Ad.findByLongi", query = "SELECT a FROM Ad a WHERE a.longi = :longi")})

 @NamedNativeQueries(
            {@NamedNativeQuery(name="Native.Ad.findCloseBy", query="SELECT * from ad  where earth_distance(ll_to_earth(?,?), ll_to_earth(ad.lati,ad.longi) ) <= ? ", resultClass = Ad.class)}
)

public class Ad implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "string_thumbnail")
    private String stringThumbnail;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "string_image")
    private String stringImage;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "cost")
    private double cost;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "status")
    private String status;
    @Basic(optional = false)
    @NotNull
    @Column(name = "posting_date")
    @Temporal(TemporalType.DATE)
    private Date postingDate;
    @Column(name = "views")
    private Integer views;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "lati")
    private Double lati;
    @Column(name = "longi")
    private Double longi;
    @JoinTable(name = "category_ad", joinColumns = {
        @JoinColumn(name = "id_ad", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "id_category", referencedColumnName = "id")})
    @ManyToMany
    private Collection<Category> categoryCollection;
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    @ManyToOne
    private User idUser;

    public Ad() {
    }

    public Ad(Integer id) {
        this.id = id;
    }

    public Ad(Integer id, String stringThumbnail, String stringImage, String name, String description, double cost, String status, Date postingDate) {
        this.id = id;
        this.stringThumbnail = stringThumbnail;
        this.stringImage = stringImage;
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.status = status;
        this.postingDate = postingDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStringThumbnail() {
        return stringThumbnail;
    }

    public void setStringThumbnail(String stringThumbnail) {
        this.stringThumbnail = stringThumbnail;
    }

    public String getStringImage() {
        return stringImage;
    }

    public void setStringImage(String stringImage) {
        this.stringImage = stringImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(Date postingDate) {
        this.postingDate = postingDate;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Double getLati() {
        return lati;
    }

    public void setLati(Double lati) {
        this.lati = lati;
    }

    public Double getLongi() {
        return longi;
    }

    public void setLongi(Double longi) {
        this.longi = longi;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Category> getCategoryCollection() {
        return categoryCollection;
    }

    public void setCategoryCollection(Collection<Category> categoryCollection) {
        this.categoryCollection = categoryCollection;
    }

    public User getIdUser() {
        return idUser;
    }

    public void setIdUser(User idUser) {
        this.idUser = idUser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ad)) {
            return false;
        }
        Ad other = (Ad) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "eshop.maven2.jpa.entities.Ad[ id=" + id + " ]";
    }
    
}
