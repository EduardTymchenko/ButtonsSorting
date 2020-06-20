The application implements a quick sort algorithm and visualizes it.

It is single page application. GWT client only.

At first screen enter numbers (X) and application generate X numbers at range from 0 to 1000.

At second screen show this arrays.

If click **Select** button array sorted step by step with 0.5 second speed.
Clicking the sort button again, will change it to increasing order.

If you click **Reset** button, you will return to the right screen.
You can change the sorting speed if you enter a value before clicking the button.

If click **Numbers** button equal or less than 30, application generate new random array of size X.

Tech stack: Java + GWT

**Run application:** 

mvn clean package

mvn gwt:devmode

*If this is your first time running a GWT application in development mode, you may be  to install the Google Web Toolkit Developer Plugin.
