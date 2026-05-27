package com.astroscope.lab.service;

import com.astroscope.lab.model.Observation;
import com.astroscope.lab.model.User;
import com.astroscope.lab.platform.access.AccessDecisionPipeline;
import com.astroscope.lab.platform.access.AccessRequestContext;
import com.astroscope.lab.platform.content.ScientificMarkupPipeline;
import com.astroscope.lab.repository.CollaborationGroupRepository;
import com.astroscope.lab.repository.ObservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ObservationService {

    private final ObservationRepository observationRepository;
    private final CollaborationGroupRepository groupRepository;
    private final AccessDecisionPipeline accessDecisionPipeline;
    private final ScientificMarkupPipeline scientificMarkupPipeline;

    public ObservationService(ObservationRepository observationRepository,
                              CollaborationGroupRepository groupRepository,
                              AccessDecisionPipeline accessDecisionPipeline,
                              ScientificMarkupPipeline scientificMarkupPipeline) {
        this.observationRepository = observationRepository;
        this.groupRepository = groupRepository;
        this.accessDecisionPipeline = accessDecisionPipeline;
        this.scientificMarkupPipeline = scientificMarkupPipeline;
    }

    public List<Observation> publicFeed() {
        return observationRepository.findByIsPrivateFalseOrderByCreatedAtDesc();
    }

    @Transactional
    public Observation create(User author, String title, String body, boolean isPrivate, String groupSlug, String target) {
        Observation obs = new Observation();
        obs.setAuthor(author);
        obs.setTitle(title);
        obs.setBodyMarkdown(body);
        obs.setPrivate(isPrivate);
        obs.setTargetObject(target);
        if (groupSlug != null && !groupSlug.isBlank()) {
            groupRepository.findBySlug(groupSlug).ifPresent(obs::setGroup);
        }
        return observationRepository.save(obs);
    }

    public Optional<Observation> findById(Long id) {
        return observationRepository.findById(id);
    }

    public boolean canView(User viewer, Observation observation, String collaborationScope) {
        return accessDecisionPipeline.permit(
                observation,
                viewer,
                new AccessRequestContext(collaborationScope, "web"));
    }

    public String renderBodyHtml(Observation observation) {
        return scientificMarkupPipeline.renderObservationBody(observation.getBodyMarkdown());
    }
}
