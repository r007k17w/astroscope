from app.models import CollaborationGroup, User
from app.platform.notify.preview import snippet_preview_renderer
from app.platform.search.member_resolver import member_directory_resolver


class GroupDirectoryService:
    def member_snippets(self, group: CollaborationGroup) -> list[dict]:
        snippets = []
        for member in group.members:
            snippets.append(
                {
                    "username": member.username,
                    "display_name": member.display_name,
                    "preview_html": snippet_preview_renderer.render_member_snippet(member.bio),
                }
            )
        return snippets

    def search_handles(self, fragment: str) -> list[dict]:
        return member_directory_resolver.resolve_handles(fragment)


group_directory_service = GroupDirectoryService()
