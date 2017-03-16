package contracts;

import agents.Agent;
import demos.Parameters;

public class RepoMatchedBook extends Repo {

    private Repo secondRepo;
    public RepoMatchedBook(Agent assetParty, Agent liabilityParty, double principal, Repo secondRepo) {
        super(assetParty, liabilityParty, principal);
        this.secondRepo = secondRepo;
    }

    @Override
    public double getLCRweight() {
        return Parameters.MATCH_BOOK_LCR;
    }

    @Override
    public void marginCall() throws FailedMarginCallException {
        // If someone asks me to perform a margin call, I just ask the matchbook repo to perform one.
        secondRepo.marginCall();

        // If the margin call fails, then the Repo has defaulted!
        //TODO What to do when matched book margin call fails?
    }
}
