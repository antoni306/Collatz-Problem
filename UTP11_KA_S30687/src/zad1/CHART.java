package zad1;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigInteger;
import java.util.List;

public class CHART extends JFrame {

    public CHART() {
        this.setSize(new Dimension(1920, 1080));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        JPanel chartPanel = new JPanel();
        chartPanel.setBackground(Color.LIGHT_GRAY);
        XYChart colatzChart=new XYChartBuilder().width(1000).height(800).title("collatz problem").xAxisTitle("x").yAxisTitle("y").build();
        chartPanel.add(new XChartPanel<>(colatzChart));
        this.add(chartPanel, BorderLayout.CENTER);

        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BorderLayout());
        eastPanel.setBackground(Color.LIGHT_GRAY);
        this.add(eastPanel, BorderLayout.EAST);
        DefaultListModel<String> listModel = new DefaultListModel<>();

        JList<String> jlist = new JList<>(listModel);
        eastPanel.add(jlist, BorderLayout.CENTER);
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlist.setPrototypeCellValue("Przykladowa");


        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        JButton deleteButton = new JButton("delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String deletedValue = jlist.getSelectedValue();
                if(deletedValue != null) {
                    System.out.println("jestem w delete"+deletedValue);
                    listModel.removeElement(deletedValue);
                    CollatzProblem.cancelThread(new BigInteger(deletedValue));
                    colatzChart.removeSeries("number: "+deletedValue);
                    chartPanel.revalidate();
                    chartPanel.repaint();
                }
            }
        });
        buttonsPanel.add(deleteButton);
        JButton cancelButton = new JButton("cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("cancel");
                CollatzProblem.cancelThread(new BigInteger(jlist.getSelectedValue()));
            }
        });
        buttonsPanel.add(cancelButton);
        JButton checkStatus = new JButton("check status");
        checkStatus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(jlist.getSelectedValue() != null) {
                    JOptionPane.showMessageDialog(null,CollatzProblem.getStatus(new BigInteger(jlist.getSelectedValue())),"Result",JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        buttonsPanel.add(checkStatus);
        JButton resultsButton = new JButton("results");
        resultsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(CollatzProblem.getStatus(new BigInteger(jlist.getSelectedValue()))== CollatzProblem.State.FINISHED) {
                    List<BigInteger> yvalues=CollatzProblem.getFuture(new BigInteger(jlist.getSelectedValue()));
                    double[] yvaluesArray = new double[yvalues.size()];
                    for(int i=0; i<yvalues.size(); i++) {
                        yvaluesArray[i]=yvalues.get(i).doubleValue();
                    }
                    colatzChart.addSeries("number: "+jlist.getSelectedValue(),getXValues(yvalues),yvaluesArray);
                    chartPanel.revalidate();
                    chartPanel.repaint();
                }
            }
        });
        buttonsPanel.add(resultsButton);
        JButton addButton = new JButton("add");

        addButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                JFrame addFrame = new JFrame("add new element");
                addFrame.setSize(100,100);
                addFrame.setLayout(new BorderLayout());
                JTextField numbField = new JTextField("select number");
                addFrame.add(numbField, BorderLayout.NORTH);
                JButton okButton = new JButton("ok");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(numbField.getText().matches("^-?\\d+$")) {
                            listModel.addElement(numbField.getText());
                            CollatzProblem.addThread(new BigInteger(numbField.getText()));

                        }else {
                            JOptionPane.showMessageDialog(null,"Error!!! Wrong type of arguments.","ERROR",JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                addFrame.add(okButton,BorderLayout.SOUTH);
                addFrame.setVisible(true);
            }
        });
        buttonsPanel.add(addButton, BorderLayout.NORTH);

        JButton startButton = new JButton("start Thread");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CollatzProblem.startThread(new BigInteger(jlist.getSelectedValue()));
            }
        });
        buttonsPanel.add(startButton);
        eastPanel.add(buttonsPanel, BorderLayout.NORTH);

        this.setVisible(true);
    }
    public static double[] getXValues(List<BigInteger> values) {
        double[] xValues = new double[values.size()];
        for(int i=0;i<values.size();i++) {
            xValues[i]=i;
        }
        return xValues;
    }
}

