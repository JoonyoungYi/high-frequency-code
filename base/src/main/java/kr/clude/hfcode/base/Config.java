package kr.clude.hfcode.base;

/**
 * Created by joonyoung.yi on 2017. 3. 25..
 */

public class Config {

    public static final int MAX_MESSAGE_INDEX = 10;
    public static final double MIN_FREQUENCY = 19000;
    public static final double MAX_FREQUENCY = 21000;
    public static final int SAMPLE_RATE = 44100;
    public static final double FREQUENCY_WINDOW = (MAX_FREQUENCY - MIN_FREQUENCY) / MAX_MESSAGE_INDEX;
}
