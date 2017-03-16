package contracts;

import agents.Agent;

public class RepoMatchedBook extends Repo {

    private Repo secondRepo;
    public RepoMatchedBook(Agent assetParty, Agent liabilityParty, double principal, Repo secondRepo) {
        super(assetParty, liabilityParty, principal);
        this.secondRepo = secondRepo;
    }


}
