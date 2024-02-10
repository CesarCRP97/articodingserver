package com.articoding.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String username;
    @Column
    @JsonIgnore
    private String password;

    @Column
    private boolean enabled = true;

    //TODO - Eliminar
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Valoration> valorationList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList;


    // TODO - createdPlaylists??
    //Nuevas listas likedLevels y downloadedLevels + getters/setters
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "levels_liked",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "level_id", referencedColumnName = "id")
    )
    private List<Level> likedLevels;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "levels_downloaded",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "level_id", referencedColumnName = "id")
    )
    private List<Level> downloadedLevels;
    // Hasta aqui


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Level> createdLevels;

    @ManyToOne()
    private Role role;

    @ManyToMany(mappedBy = "students")
    private List<ClassRoom> classRooms;

    @ManyToMany(mappedBy = "teachers")
    private List<ClassRoom> ownerClassRooms;

    //TODO - OneToMany list of students of a teacher.

    /**
     *
     */

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    //TODO - Quitar junto a Valoration & Comment
    public List<Valoration> getValorationList() {
        return valorationList;
    }

    public void setValorationList(List<Valoration> valorationList) {
        this.valorationList = valorationList;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }
    //Hasta aqui


    public List<Level> getLikedLevels() {
        return likedLevels;
    }

    public void setLikedLevels(List<Level> likedLevels) {
        this.likedLevels = likedLevels;
    }

    public List<Level> getDownloadedLevels() {
        return downloadedLevels;
    }

    public void setDownloadedLevels(List<Level> downloadedLevels) {
        this.downloadedLevels = downloadedLevels;
    }

    public List<Level> getCreatedLevels() {
        return createdLevels;
    }

    public void setCreatedLevels(List<Level> createdLevels) {
        this.createdLevels = createdLevels;
    }

    public List<ClassRoom> getClasses() {
        return classRooms;
    }

    public void setClasses(List<ClassRoom> classRooms) {
        this.classRooms = classRooms;
    }

    public List<ClassRoom> getOwnerClasses() {
        return ownerClassRooms;
    }

    public void setOwnerClasses(List<ClassRoom> ownerClassRooms) {
        this.ownerClassRooms = ownerClassRooms;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<ClassRoom> getClassRooms() {
        return classRooms;
    }

    public void setClassRooms(List<ClassRoom> classRooms) {
        this.classRooms = classRooms;
    }

    public List<ClassRoom> getOwnerClassRooms() {
        return ownerClassRooms;
    }

    public void setOwnerClassRooms(List<ClassRoom> ownerClassRooms) {
        this.ownerClassRooms = ownerClassRooms;
    }
}