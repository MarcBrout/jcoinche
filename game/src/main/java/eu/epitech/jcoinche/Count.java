package eu.epitech.jcoinche;

public final class Count {

    private static int[] mNormalValue = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160};

    public static int getRealScore(int value) {
        int i = 0;

        while (i < 17 && mNormalValue[i] <= value){
            i++;
        }
        if (i == 17)
            return (160);
        if (value - mNormalValue[i - 1] < mNormalValue[i] - value)
            return (mNormalValue[i - 1]);
        else
            return (mNormalValue[i]);
    }
}
