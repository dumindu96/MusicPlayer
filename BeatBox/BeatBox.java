import java.awt.*;
import javax.swing.*;

import javafx.event.ActionEvent;

import javax.sound.midi.*;
import java.util.*;
import java.awt.event.*;

public class BeatBox
{
    JPanel mainPanel;
    ArrayList<JCheckBox> checkBoxList;
    Sequencer sequencer;
    Sequence seq;
    Track track;
    JFrame theFrame;

    String[] instrumentNames = {"Bass Drum","Closed High Hat","Open High hat","Acoustic Snare","Crash Cymbal","Hand Clap","High Tom","High Bongo","Maracas","Whistle","Low Congo","Cowbell","Vibraslep","Low-mid Tom","High Agogo","Open Hi Conga"};
    int[] instruments = {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};

    public static void main(String[] args)
    {
        new BeatBox2.buildGUI();
    }

    public void buildGUI()
    {
        theFrame =  new JFrame("Cyber Beatbox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        checkBoxList = new ArrayList<JCheckBox>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS);

        JButton start = new JButton("Start");
        start.addActionListener(new MyStartListner());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListner());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(new MyUpTempoListner());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(new MyDownTempoListner());
        buttonBox.add(downTempo);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for(int i = 0; i<16; i++)
        {
            nameBox.add(new Label(instrumentNames[i]));
        }

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);

        GridLayout grid = new GridLayout(16,16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        for(int i = 0; i<256; i++)
        {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkBoxList.add(c);
            mainPanel.add(c);
        }

        setUpMidi();

        theFrame.setBounds(50, 50, 300, 300);
        theFrame.pack();
        theFrame.setVisible(true);
    }

    public void setUpMidi()
    {
        try
        {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            seq = new Sequence(Sequence.PPQ, 4);
            track=seq.createTrack();
            sequencer.setTempoInBPM(120);;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void buildTrackAndStart()
    {
        int[] trackList = null;
        seq.deleteTrack(track);
        track=seq.createTrack();

        for(int i=0; i<16; i++)
        {
            trackList = new int[16];
            int key = instruments[i];

            for(int j= 0; j<16; j++)
            {
                JCheckBox jc = (JCheckBox) checkBoxList.get(j+(16*1));
                if(jc.isSelected())
                {
                    trackList[j]=key;
                }
                else
                {
                    trackList[j]=0;
                }
            }
            makeTracks(trackList);
            track.add(makeEvent(176,1,127,0,16));
        }
        track.add(makeEvent(192,9,1,0,15));
        try
        {
            sequencer.setSequence(seq);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public class MyStartListner implements ActionListener
    {
        public void actionPerformed(ActionEvent a)
        {
            buildTrackAndStart();
        }
    }

    public class MyStopListner implements ActionListener
    {
        public void actionPerformed(ActionEvent a)
        {
            sequencer.stop();
        }
    }

    public class MyUpTempoListner implements ActionListener
    {
        public void actionPerformed(ActionEvent a)
        {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor*1.03));
        }
    }

    public class MyDownTempoListner implements ActionListener
    {
        public void actionPerformed(ActionEvent a)
        {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor*0.97));
        }
    }

}