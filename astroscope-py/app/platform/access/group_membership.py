class GroupMembershipVerifier:
    def delegator_may_assign(self, delegator, group) -> bool:
        if delegator.role == "ADMIN":
            return True
        if group.owner_id == delegator.id:
            return True
        return delegator in group.members


group_membership_verifier = GroupMembershipVerifier()
