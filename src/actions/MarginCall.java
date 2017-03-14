package actions;

import contracts.Repo;

public class MarginCall extends Action {

    public MarginCall(Repo repo) {
    }

    private Repo repo;

    @Override
    public void perform() {
        repo.marginCall();

    }

    @Override
    public double getMax() {
        return -1;
    }

    @Override
    public String getName() {
        return "Margin call";
    }

    @Override
    public void print() {

    }
}
