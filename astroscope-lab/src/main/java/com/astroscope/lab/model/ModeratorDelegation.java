package com.astroscope.lab.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "moderator_delegations")
public class ModeratorDelegation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private User delegator;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private User delegate;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private CollaborationGroup group;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private Instant grantedAt = Instant.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getDelegator() {
        return delegator;
    }

    public void setDelegator(User delegator) {
        this.delegator = delegator;
    }

    public User getDelegate() {
        return delegate;
    }

    public void setDelegate(User delegate) {
        this.delegate = delegate;
    }

    public CollaborationGroup getGroup() {
        return group;
    }

    public void setGroup(CollaborationGroup group) {
        this.group = group;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getGrantedAt() {
        return grantedAt;
    }

    public void setGrantedAt(Instant grantedAt) {
        this.grantedAt = grantedAt;
    }
}
