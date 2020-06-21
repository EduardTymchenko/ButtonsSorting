package Gransoft.test.gwt.SortingButtons;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.core.client.EntryPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The QuickSortButtons class renders an algorithm for quickly sorting an array of size X according to user input.
 * This is Single Page Application.
 */
public class QuickSortButtons implements EntryPoint {
    private static Logger logger = Logger.getLogger("");

    /**
     * Entry point in GWT application
     */
    public void onModuleLoad() {
        Intro intro = new Intro();
        RootPanel.get("mainContent").add(new Intro());
        intro.setFocusNumberField();
    }

    /**
     * A method that removes all components from the root container and installs the components of the submitted page.
     * @param showWindow  submitted page
     */
    private void showWindow(Composite showWindow) {
        RootPanel.get("mainContent").remove(0);
        RootPanel.get("mainContent").add(showWindow);
    }

    /**
     * Class Intro page
     */
    public class Intro extends Composite {
        private final FlowPanel introContainer = new FlowPanel();
        private final Label questionLabel = new Label();
        private final TextBox numberField = new TextBox();
        private final Label errorLabel = new Label();
        private final Button enterButton = new Button("Enter");

        public Intro() {
            buildIntroWindow();
        }
        /**
         * Intro page Elements Rendering Method
         */
        private void buildIntroWindow() {
            introContainer.addStyleName("introContainer");
            questionLabel.setText("How many numbers to display?");
            questionLabel.addStyleName("questionLabel");
            numberField.setMaxLength(4);
            errorLabel.addStyleName("errorLabel");

            introContainer.add(questionLabel);
            introContainer.add(numberField);
            introContainer.add(errorLabel);
            introContainer.add(enterButton);
            initWidget(introContainer);

            enterButton.addClickHandler(clickEvent -> goScreenSort());
            numberField.addKeyUpHandler(keyUpEvent -> {
                if (keyUpEvent.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    goScreenSort();
                }
            });
        }
        /**
         * The method that loads the sorting page when the length of the array is entered correctly
         * or generates an input error message.
         */
        private void goScreenSort(){
                errorLabel.setText("");
                String inputNumbers = numberField.getText();
                if (isValidNumber(inputNumbers, 1, 1000)) {
                    QuickSortButtons.this.showWindow(new Sort(Integer.parseInt(inputNumbers)));
                } else {
                    errorLabel.setText("Please enter number from 1 to 1000 [1,1000]");
                    numberField.setFocus(true);
                    numberField.selectAll();
                }
        }
        /**
         * Array Length Check Method
         * @param inputNumber input parameter String
         * @param minNumber minimum value
         * @param maxNumber maximum value
         * @return boolean
         */
        private boolean isValidNumber(String inputNumber, int minNumber, int maxNumber) {
            logger.log(Level.SEVERE, "Enter numbers of display: " + inputNumber);
            try {
                int numbersToDisplay = Integer.parseInt(inputNumber);
                if (numbersToDisplay < minNumber || numbersToDisplay > maxNumber) return false;
            } catch (NumberFormatException ex) {
                return false;
            }
            return true;
        }

        public void setFocusNumberField() {
            numberField.setFocus(true);
        }
    }

    /**
     * Class Sort page
     */
    public class Sort extends Composite {
        /**
         * Time show step Quick Sort, mc. Default 500 mc.
         */
        private final int showTimeDefault = 500;
        /**
         * List of number buttons for sorting shows
         */
        private List<Button> numberButtonsList = new ArrayList<>();
        private FlowPanel sortContainer = new FlowPanel();
        private final Button sortButton = new Button("Sort");
        private final Button resetButton = new Button("Reset");
        private final TextBox speedField = new TextBox();

        private Random random = new Random();
        private boolean isIncreasingSort;
        private boolean isSortingShow;

        public Sort(int numbersOfDisplay) {
            buildSortWindow(numbersOfDisplay);
        }
        /**
         * Sort page Elements Rendering Method
         */
        private void buildSortWindow(int numbersOfDisplay) {
            sortContainer.addStyleName("sortContainer");
            sortContainer.add(buildNumbersBlock(numbersOfDisplay));
            sortContainer.add(buildButtonBlock());
            initWidget(sortContainer);
        }
        /**
         * Method that creates a panel of numbered buttons for visualization on the sorting page
         * @param numbersOfDisplay number of buttons
         * @return  Widget
         */
        private Widget buildNumbersBlock(int numbersOfDisplay) {
            List<Integer> listRandomNumbers = generateArrayInt(numbersOfDisplay, 1, 1000);
            numberButtonsList.clear();
            FlowPanel numbersBlockContainer = new FlowPanel();
            numbersBlockContainer.addStyleName("numbersBlockContainer");
            FlowPanel numberBlockGroup = null;
            for (int i = 0; i < listRandomNumbers.size(); i++) {
                if (i % 10 == 0) {
                    numberBlockGroup = new FlowPanel();
                    numberBlockGroup.addStyleName("numberBlock");
                    numbersBlockContainer.add(numberBlockGroup);
                }
                Button numberBtn = new Button(listRandomNumbers.get(i).toString());
                numberBtn.addClickHandler(clickEvent -> {
                    if (isSortingShow) return;
                    isIncreasingSort = false;
                    int numberClick = Integer.parseInt(clickEvent.getRelativeElement().getInnerText());
                    logger.log(Level.SEVERE, "Click number button with number=" + numberClick);
                    if (numberClick > 30) {
                        PopupPanel popupPanel = new PopupPanel(true);
                        popupPanel.add(new Label("Please select a value smaller or equal to 30."));
                        popupPanel.show();
                        int popupPanelX = clickEvent.getClientX();
                        int popupPanelY = clickEvent.getClientY() - popupPanel.getElement().getAbsoluteBottom();
                        popupPanel.setPopupPosition(popupPanelX, popupPanelY);
                    } else {
                        sortContainer.remove(0);
                        sortContainer.insert(buildNumbersBlock(numberClick),0);
                    }
                });
                numberButtonsList.add(numberBtn);
                numberBlockGroup.add(numberBtn);
            }
            return numbersBlockContainer;
        }

        /**
         *  Method that creates a panel of control buttons for visualization on the sort page.
         * @return  Widget
         */
        private Widget buildButtonBlock() {
            FlowPanel buttonBlockContainer = new FlowPanel();
            buttonBlockContainer.addStyleName("buttonBlock");
            sortButton.addStyleName("btnSort");
            resetButton.addStyleName("btnSort");
            buttonBlockContainer.add(sortButton);
            buttonBlockContainer.add(resetButton);
            // Button "Sort"
            sortButton.addClickHandler(clickEvent -> {
                logger.log(Level.SEVERE, "Click button \"Sort\"");
                List<Integer> listForSort = numberButtonsList.stream()
                        .map(el -> Integer.parseInt(el.getText()))
                        .collect(Collectors.toList());
                visualSort(getListStepsSort(listForSort, 0, isIncreasingSort), generateTimeShow());
                isIncreasingSort = !isIncreasingSort;
            });
            // Button "Reset"
            resetButton.addClickHandler(clickEvent -> {
                logger.log(Level.SEVERE, "Click button \"Reset\"");
                Intro intro = new Intro();
                QuickSortButtons.this.showWindow(intro);
                intro.setFocusNumberField();
            });
            buttonBlockContainer.add(speedSortBuildBlock());
            return buttonBlockContainer;
        }

        /**
         * Method that creates a sort animation speed input panel for rendering on a sort page.
         * @return Widget
         */
        private Widget speedSortBuildBlock() {
            FlowPanel blockSpeedSort = new FlowPanel();
            Label labelSpeedField = new Label("Enter speed show sort [1,30] int \n (default 0.5 s):");
            labelSpeedField.addStyleName("speedSort");
            blockSpeedSort.add(labelSpeedField);
            speedField.setMaxLength(2);
            speedField.addStyleName("speedSort");
            blockSpeedSort.add(speedField);
            return blockSpeedSort;
        }

        /**
         * Method that visualizes sorting steps by changing the styles of numbered buttons.
         * @param listStepsSort list steps sorting
         * @param timeShow time show one step, mc (integer)
         */
        private void visualSort(List<StepSort> listStepsSort, int timeShow) {
            Timer stepTimer = new Timer() {
                private int step = 1;

                public void run() {
                    resetStyleShowSort(listStepsSort, step - 1);
                    if (step >= listStepsSort.size()) {
                        disableAllButtonPage(false);
                        this.cancel();
                        return;
                    }
                    logger.log(Level.SEVERE, "Step sort:\n" + listStepsSort.get(step));
                    setStyleShowSort(listStepsSort, step);
                    step++;
                }
            };
            disableAllButtonPage(true);
            setStyleShowSort(listStepsSort, 0);
            stepTimer.scheduleRepeating(timeShow);
        }

        /**
         * Method that sets styles for the current sorting step.
         * @param listSteps list steps sorting
         * @param stepShow number step
         */
        private void setStyleShowSort(List<StepSort> listSteps, int stepShow) {
            StepSort stepObj = listSteps.get(stepShow);
            for (int k = stepObj.low; k <= stepObj.high; k++) {
                numberButtonsList.get(k).addStyleName("sortArray");
            }
            numberButtonsList.get(stepObj.indexPaviot).addStyleName("paviot");
            numberButtonsList.get(stepObj.i).addStyleName("pointer");
            numberButtonsList.get(stepObj.j).addStyleName("pointer");
            if (stepObj.isSwap) {
                updateNumberButtonsAll(stepObj.integerList);
                numberButtonsList.get(stepObj.i).addStyleName("swap");
                numberButtonsList.get(stepObj.j).addStyleName("swap");
            }
        }

        /**
         * Method that resets styles to the default value for the current sorting step
         * @param listSteps list steps sorting
         * @param stepShow number step
         */
        private void resetStyleShowSort(List<StepSort> listSteps, int stepShow) {
            StepSort stepObj = listSteps.get(stepShow);
            for (int k = stepObj.low; k <= stepObj.high; k++) {
                numberButtonsList.get(k).removeStyleName("sortArray");
            }
            numberButtonsList.get(stepObj.i).removeStyleName("pointer");
            numberButtonsList.get(stepObj.i).removeStyleName("swap");
            numberButtonsList.get(stepObj.j).removeStyleName("pointer");
            numberButtonsList.get(stepObj.j).removeStyleName("swap");
            numberButtonsList.get(stepObj.indexPaviot).removeStyleName("paviot");
        }

        /**
         * Method that sets the time to render a sorting step.
         * @return time, mc
         */
        public int generateTimeShow() {
            try {
                int speedForm = Integer.parseInt(speedField.getText());
                if (speedForm > 0 && speedForm <= 30) return speedForm * 1000;
            } catch (NumberFormatException ex) {
                return showTimeDefault;
            }
            return showTimeDefault;
        }
        /**
         * Method that generates random values for array elements from a given range.
         * Check input value, at least one value should be equal or less than 30.
         * @param length number of array elements
         * @param rangeMin  minimum value
         * @param rangeMax maximum value
         * @return list integer numbers
         */
        private List<Integer> generateArrayInt(int length, int rangeMin, int rangeMax) {
            logger.log(Level.SEVERE, "Generate integer array length: " + length + ", range [" + rangeMin + "," + rangeMax + "]");
            boolean isOkArray = false;
            List<Integer> arrayInt = new ArrayList<>();
            while (!isOkArray) {
                arrayInt.clear();
                for (int i = 0; i < length; i++) {
                    int randomNumber = random.nextInt(rangeMax - rangeMin + 1) + rangeMin;
                    arrayInt.add(randomNumber);
                    if (randomNumber <= 30) isOkArray = true;
                }
            }
            logger.log(Level.SEVERE, "Generated integer array: " + arrayInt);
            return arrayInt;
        }

        private void updateNumberButtonsAll(List<Integer> arrNumbers) {
            for (int i = 0; i < numberButtonsList.size(); i++) {
                numberButtonsList.get(i).setText(arrNumbers.get(i).toString());
            }
        }

        private void disableAllButtonPage(boolean isBlock) {
            sortButton.setEnabled(!isBlock);
            resetButton.setEnabled(!isBlock);
            isSortingShow = isBlock;
        }
        /**
         * Class for storing complete information about the sorting step
         */
        private class StepSort {
            private int i;
            private int j;
            private int indexPaviot;
            private int low;
            private int high;
            private boolean isSwap;
            private List<Integer> integerList;

            public StepSort(int low, int high, int i, int j, int indexPaviot, boolean isSwap, List<Integer> integerList) {
                this.low = low;
                this.high = high;
                this.i = i;
                this.j = j;
                this.indexPaviot = indexPaviot;
                this.isSwap = isSwap;
                this.integerList = new ArrayList<>(integerList);
            }

            public StepSort(int low, int high, int i, int j, int indexPaviot, List<Integer> integerList) {
                this.low = low;
                this.high = high;
                this.i = i;
                this.j = j;
                this.indexPaviot = indexPaviot;
                this.integerList = new ArrayList<>(integerList);
            }

            @Override
            public String toString() {
                return "{" + integerList + " low=" + low + " high=" + high + " i=" + i +  ", j=" + j +
                        ", indexPaviot=" + indexPaviot + ", isSwap=" + isSwap + "}" + "\n";
            }
        }
        /**
         * Method creates a list of sorting steps for visualization.
         * @param listInteger input array to sort
         * @param numberAlgorithmForPaviot number of strategy select pivot element
         * @param isIncreasing order sort (true - Increasing, false - decrease)
         * @return list sorting steps
         */
        private List<StepSort> getListStepsSort(List<Integer> listInteger, int numberAlgorithmForPaviot, boolean isIncreasing) {

            class QuickSort {
                private List<StepSort> listStepsSort = new ArrayList<>();
                /**
                 * Method that executes a quick sort algorithm using recursion
                 * @param integerList input array to sort
                 * @param low start index for sorting array elements
                 * @param high last index for sorting array elements
                 * @param numberAlgorithmForPaviot number of strategy select pivot
                 * @param isIncreasing order sort, (true - Increasing, false - decrease)
                 */
                private void sortIntArray(List<Integer> integerList, int low, int high, int numberAlgorithmForPaviot, boolean isIncreasing) {
                    if (integerList.isEmpty() || low >= high)
                        return;
                    /**
                     * select pivot element
                     */
                    int indexPaviot = indexPivot(low, high, numberAlgorithmForPaviot);
                    int pivot = integerList.get(indexPaviot);
                    /**
                     * split into subarrays that are larger and smaller than the Pivot element
                     */
                    int i = low;
                    int j = high;
                    listStepsSort.add(new StepSort(low, high, i, j, indexPaviot, integerList));
                    while (i <= j) {

                        while ((integerList.get(i) > pivot ^ isIncreasing) && integerList.get(i) != pivot) {
                            i++;
                            listStepsSort.add(new StepSort(low, high, i, j, indexPaviot, integerList));
                        }

                        while ((integerList.get(j) < pivot ^ isIncreasing) && integerList.get(j) != pivot) {
                            j--;
                            listStepsSort.add(new StepSort(low, high, i, j, indexPaviot, integerList));
                        }
                        if (i < j) {
                            /**
                             * swap for Pivot==low
                             */
                            if (i == indexPaviot) {
                                integerList.add(i, integerList.get(j));
                                integerList.remove(j + 1);
                                listStepsSort.add(new StepSort(low, high, i, j, indexPaviot, true, integerList));
                                indexPaviot = ++i;
                                /**
                                 * swap for Pivot==high
                                 */
                            } else if (j == indexPaviot) {
                                integerList.add(j + 1, integerList.get(i));
                                integerList.remove(i);
                                listStepsSort.add(new StepSort(low, high, i, j, indexPaviot, true, integerList));
                                indexPaviot = --j;
                                /**
                                 * swap for Pivot in the middle
                                 */
                            } else {
                                Collections.swap(integerList, i, j);
                                logger.log(Level.SEVERE, "Step sort: " + integerList + " i=" + i + " j=" + j + " swap");
                                listStepsSort.add(new StepSort(low, high, i, j, indexPaviot, true, integerList));
                                i++;
                                j--;

                            }
                            listStepsSort.add(new StepSort(low, high, i, j, indexPaviot, integerList));
                        } else {
                            i++;
                        }
                        /**
                         * or Pivot==high if sorted
                         */
                        if (j == indexPaviot && j == high) j--;
                    }
                    /**
                     * recursion call to sort left and right arrays
                     */
                    if (low < j) //left
                        sortIntArray(integerList, low, j, numberAlgorithmForPaviot, isIncreasing);
                    if (high > i) //right
                        sortIntArray(integerList, i, high, numberAlgorithmForPaviot, isIncreasing);
                }

                /**
                 * Method that allows you to choose a strategy for determining Pivot
                 * @param low start index for sorting array elements
                 * @param high last index for sorting array elements
                 * @param numberAlgorithm number of strategy select pivot:
                 * 1 - Pivot = low;
                 * 2 - Pivot = high;
                 * 3 - Pivot = random;
                 * any - Pivot = middle, it is default.
                 * @return index Pivot
                 */
                private int indexPivot(int low, int high, int numberAlgorithm) {
                    switch (numberAlgorithm) {
                        case 1:
                            return low;
                        case 2:
                            return high;
                        case 3:
                            return new Random().nextInt(high - low + 1) + low;
                        default:
                            return low + (high - low) / 2;
                    }
                }
            }
            QuickSort quickSort = new QuickSort();
            quickSort.sortIntArray(listInteger, 0, listInteger.size() - 1, numberAlgorithmForPaviot, isIncreasing);
            return quickSort.listStepsSort;
        }
    }
}
