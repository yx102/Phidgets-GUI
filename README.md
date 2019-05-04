# Phidgets-GUI
This code is the GUI I designed for my experiment. In my experiment environment, I used four load cells to form a cage. A steel ball is placed on a inclined 2-DOF surface. By measuring the forced required to balenced the ball on the surface, along with the weight of the ball, the slope of the surface could be calculated.

Thus, in the GUI, the user could choose the channel from which the data to be collected. After the collect bottom is pressed, the channel will be opened for five seconds, and the average voltage would be calculated and displayed on the interface. In addition, all load cells have been calibrated before, hence, the equations to translate the voltage to weight are built in the code. The ratio between the force to balance the ball and the weight of the ball would be display on the channel. 

The interface also has a function of "zero". Just as normal commercial load cells, it is recommend to first measure the voltage when there is no force on the load cell as a "base line". The built-in equation would adjust itself through this "zero" button.
The status display would present all previous commends that the interface did before, and also it will show the error information when something is wrong. Of course, if one prefer to have a clean status display, a "clear" button is there.

The "phidget22.jar" is the library provided by the manufacturer