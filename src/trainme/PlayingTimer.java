/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trainme;

import java.awt.Color;
import java.awt.Toolkit;
import static java.lang.Math.abs;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import javax.sound.sampled.Clip;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import org.python.core.PyObject;

/**
 *
 * @author Sajeewa
 */
public class PlayingTimer extends Thread {

    private DateFormat dateFormater = new SimpleDateFormat("HH:mm:ss");
    private boolean isRunning = false;
    private boolean isPause = false;
    private boolean isReset = false;
    private long startTime;
    private long pauseTime;

    private JLabel labelRecordTime;
    private JSlider slider;
    private HashMap<String, String> map;
    private boolean playMode;
    private Clip audioClip;
    private JPanel jPanelView;

    private ArrayList<JLabel> playLabels;
    private JLabel pyLabel;
    private int posTolerance;

    PythonConnection pythonConnection;

    public void setAudioClip(Clip audioClip) {
        this.audioClip = audioClip;
    }

    PlayingTimer(JLabel labelRecordTime, JSlider slider, boolean playMode,
            ArrayList<JLabel> playLabels, JLabel pyLabel, JPanel jPanelView, int posTolerance) {

        this.labelRecordTime = labelRecordTime;
        this.slider = slider;
        this.playMode = playMode;
        this.playLabels = playLabels;
        this.pyLabel = pyLabel;
        this.pyLabel.setBackground(Color.CYAN);
        this.jPanelView = jPanelView;
        this.posTolerance = posTolerance;
        this.pythonConnection = new PythonConnection();
    }

    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }

    public void run() {
        isRunning = true;

        startTime = System.currentTimeMillis();

        while (isRunning) {
            try {
                Thread.sleep(100);
                if (!isPause) {
                    if (audioClip != null && audioClip.isRunning()) {
                        labelRecordTime.setText(toTimeString());

                        setMovement();
                        if (playMode) {
                            setView(labelRecordTime.getText());
                        }

//                        System.out.println("label 1 during the thread:" + View.colorLabels.get(0).getX() + "," + View.colorLabels.get(0).getY());
                        int currentSecond = (int) audioClip.getMicrosecondPosition() / 1_000_000;
                        slider.setValue(currentSecond);
                    }
                } else {
                    pauseTime += 100;
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                if (isReset) {
                    slider.setValue(0);
                    labelRecordTime.setText("00:00:00");
                    isRunning = false;
                    break;
                }
            }
        }
    }

    /**
     * Reset counting to "00:00:00"
     */
    void reset() {
        isReset = true;
        isRunning = false;
    }

    void pauseTimer() {
        isPause = true;
    }

    void resumeTimer() {
        isPause = false;
    }

    /**
     * Generate a String for time counter in the format of "HH:mm:ss"
     *
     * @return the time counter
     */
    private String toTimeString() {
        long now = System.currentTimeMillis();
        Date current = new Date(now - startTime - pauseTime);
        dateFormater.setTimeZone(TimeZone.getTimeZone("GMT"));
        String timeCounter = dateFormater.format(current);
        return timeCounter;
    }

    int i = 0;
    int x, y, gapX, gapY;
    String labelNumber;
//        int y = 0;

    private void setView(String currentTime) {

        if (map.containsKey(currentTime)) {
            String[] coordinate = map.get(currentTime).split(",");
            x = Integer.parseInt(coordinate[0]);
            y = Integer.parseInt(coordinate[1]);
            labelNumber = coordinate[2];

            gapX = abs(x - pyLabel.getX());
            gapY = abs(y - pyLabel.getY());

            for (JLabel label : playLabels) {
                if (label.getText().equals(labelNumber)) {
                    jPanelView.setLayout(null);
                    label.setLocation(x - 5, y - 5);
                    label.setBackground(Color.RED);

                    if (gapX > posTolerance || gapY > posTolerance) {
                        alert();
                    }

                }
            }
        }
    }

    private void setMovement() {
        String cords = pythonConnection.getCoordinates();
        String[] pyCoordinates = cords.split(" ");
        System.out.println(cords);
        pyLabel.setLocation(Integer.parseInt(pyCoordinates[0]) , Integer.parseInt(pyCoordinates[1]) + 250);

    }

    private void alert() {
        Toolkit.getDefaultToolkit().beep();
    }
}
