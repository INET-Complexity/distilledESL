package agents;

import contracts.Repo;

public interface CanPledgeCollateral {
    public void putMoreCollateral(double value, Repo repo);
}
