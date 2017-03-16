package agents;

import contracts.Repo;

public interface CanPledgeCollateral {
    void putMoreCollateral(double value, Repo repo);
    void withdrawCollateral(double value, Repo repo);
    double getMaxUnencumberedHaircuttedCollateral();

}
