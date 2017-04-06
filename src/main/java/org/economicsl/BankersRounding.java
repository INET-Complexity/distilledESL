package org.economicsl;

import static java.lang.Math.abs;

/**
 * Created by taghawi on 04/04/17.
 */
public class BankersRounding {
    public static int bankersRounding(double value) {
        {
            int s = (int)value;
            double t = abs(value - s);

            if ((t < 0.5) || (t == 0.5 && s % 2 == 0))
            {
                return s;
            }
            else
            {
                if (value < 0)
                {
                    return s - 1;
                }
                else
                {
                    return s + 1;
                }
            }
        }
    }
}
