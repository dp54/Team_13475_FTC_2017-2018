package org.firstinspires.ftc.teamcode;

/**
 * Created by Aryeh on 10/1/17.
 */

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuMarkInstanceId;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.Locale;


@TeleOp(name="Manual", group="TeleOp")
public class MecanumManual extends LinearOpMode {

    protected Servo leftBlockGrabber;
    protected Servo rightBlockGrabber;
    protected Servo frontStickServo;
    protected Servo frontAngleServo;
    protected Servo relicGrabberServo;
    //protected Servo frontStickServo;

    //protected Servo sideStick;
    protected DcMotor motorFrontLeft;
    protected DcMotor motorFrontRight;
    protected DcMotor motorBackLeft;
    protected DcMotor motorBackRight;
    protected DcMotor blockRaiser;
    protected DcMotor relicExtender;
    protected DcMotor sideStickMotor;
    protected DcMotor relicRaiser;

    protected ColorSensor myColorSensor;
    protected DigitalChannel frontStickButton;
    protected DigitalChannel relicRaiserMax;


    //private ColorSensor myColorSensor;
    private VuforiaLocalizer vuforia;
    private VuforiaTrackables relicTrackables;
    private VuforiaTrackable relicTemplate;
    ///If the claw is closed
    private boolean clawClosed = false;
    private boolean xPressed = false;
    private boolean relicClawClosed = false;
    private boolean switchFront = false;
    private boolean leftBumperPressed = false;
    private boolean rightBumperPressed = false;

    MotorSpeeds speed;



    @Override
    public void runOpMode() throws InterruptedException {
        //Init
        //Servos
        hardwareSetup();

        /*
        //Setup VuMark detection
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "AcBbjLb/////AAAAGUE95xAvpEQzuMrgrlfyB3pATdcFCbND7+gUm8qqRk/0O4nOKmK9NdEKNpY+27LaHPFRVrULXyQqqaUr9Vm7gtyncPMBYZo3FAfYcXboDQXtEdBOHG3HLYmczuv3/k0MUQ7PDtuNj9KlQF84vB3ZpMQCfbqMVaXGdn1rLTCiOFtDdLhK9QGC315hgzV9VsbJ0wzwwlDabJBq5+aFNFhw4gF3YS97FG6fHiMGIJ4piGQJXwh+jKuV0A7ZHoqysxs2xsV9iUSOEbsawRXo/Lg1aXw1B8SZ6T6P6oqdvnwFYXSpGT0LKkNi9K6/7g/MiFkJLcYFpVNxz6i9gueh5c4ZKaYMHwVIbW6x/cmVFSQJSPZd";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);
        relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate");*/

        speed  = new MotorSpeeds(motorFrontLeft,motorFrontRight,motorBackLeft,motorBackRight);

        waitForStart();
        //start

        //Extend color senser arm slightly to get out of way
        //MecanumAutonomus.moveDcMotorEncoded(sideStickMotor,0.2,-100);

        //extend relic extender slightly
//        relicExtender.setPower(-0.5);
//        sleep(500);
//        relicExtender.setPower(0.0);




        while(opModeIsActive()) {
            //Loop

            if(Math.abs(gamepad1.right_stick_x) >= 0.01 || Math.abs(gamepad1.right_stick_y) >= 0.01) {
                rightJoystick();
            }


            else if (gamepad1.left_trigger > 0.01) {
                leftTrigger(gamepad1.left_trigger);
                //Rotate left

            }
            else if (gamepad1.right_trigger > 0.01) {
                //rotate right
                rightTrigger(gamepad1.right_trigger);

            }

            else {
                speed.myStop();
            }

            if(gamepad1.a) {
                //move block riser down
                blockRaiser.setPower(0.2);
            }
            else if(gamepad1.y) {
                //Move block riser up
                blockRaiser.setPower(-0.3);
            }

            else {
                blockRaiser.setPower(0.0);
            }

            if(gamepad1.x) {
                //open claw
                if(!xPressed) {
                    xPressed = true;
                    if(clawClosed) {
                        rightBlockGrabber.setPosition(0.4);
                        leftBlockGrabber.setPosition(0.3);

                        clawClosed = false;
                    }
                    else {
                        rightBlockGrabber.setPosition(0.6);
                        leftBlockGrabber.setPosition(0.0);
                        clawClosed = true;
                    }
                }


            }
            else {
                xPressed = false;
            }

            if(gamepad1.dpad_up) {
                //Relic riser up
                if(relicRaiserMax.getState() == true) {
                    relicRaiser.setPower(-1.0);
                }
                else {
                    relicRaiser.setPower(0.0);
                }
                telemetry.addData("Dpad up", gamepad1.dpad_up);

            }
            else if(gamepad1.dpad_down) {
                //Relic riser down
                telemetry.addData("Dpad down", gamepad1.dpad_down);
                relicRaiser.setPower(1.0);
            }
            else {
                relicRaiser.setPower(0.0);
            }
            if(gamepad1.dpad_right) {
                //Extend relic
                relicExtender.setPower(0.5);

            }
            else if(gamepad1.dpad_left) {
                //Retract relic
                relicExtender.setPower(-0.5);
            }
            else {
                relicExtender.setPower(0.0);
            }

            if(gamepad1.right_bumper) {
                if(!rightBumperPressed) {
                    rightBumperPressed = true;
                    switchFront = !switchFront;

                }
                //Switch back to front


            }
            else {
                rightBumperPressed = false;
            }

            if(gamepad1.left_bumper) {
                if(!leftBumperPressed) {
                    leftBumperPressed = true;
                    if(relicClawClosed) {
                        //open claw
                        relicGrabberServo.setPosition(0.6);
                        relicClawClosed = false;
                    }
                    else {
                        relicGrabberServo.setPosition(0.0);
                        relicClawClosed = true;
                    }


                }



            }
            else {
                leftBumperPressed = false;
            }





        /*
        //Check for VuMark
        RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
        switch (vuMark) {
            case UNKNOWN:
                telemetry.addData("No VU Mark Visible","");
                break;
            case LEFT:
                telemetry.addData("Left VU Mark Visible","");
                break;
            case CENTER:
                telemetry.addData("Center VU Mark Visible","");
                break;
            case RIGHT:
                telemetry.addData("Right VU Mark Visible","");
                break;
        }

        //telemetry.addData("Color: ", getColor());
        telemetry.update();
        */

        }





    }

    /*
    @Override
    public void init_loop() {

    }

    @Override
    public void start() {
        //run when user presses start
        //relicTrackables.activate();

        //Extend relic slightly at the beginging

    }

    @Override
    public void loop() {
        if(Math.abs(gamepad1.right_stick_x) >= 0.01 || Math.abs(gamepad1.right_stick_y) >= 0.01) {
            rightJoystick();
        }


        else if (gamepad1.left_trigger > 0.01) {
            leftTrigger(gamepad1.left_trigger);
            //Rotate left

        }
        else if (gamepad1.right_trigger > 0.01) {
            //rotate right
            rightTrigger(gamepad1.right_trigger);

        }
        else if(gamepad1.a) {
            //move block riser down
            blockRaiser.setPower(0.2);
        }
        else if(gamepad1.y) {
            //Move block riser up
            blockRaiser.setPower(-0.3);
        }
        else if(gamepad1.x) {
            //open claw

            rightBlockGrabber.setPosition(0.3);
            leftBlockGrabber.setPosition(0.2);




        }

        else if(gamepad1.b) {

            //Close claw


            rightBlockGrabber.setPosition(0.5);
            leftBlockGrabber.setPosition(0.0);
        }

        else if(gamepad1.dpad_up) {
            //Relic riser up
            telemetry.addData("Dpad up", gamepad1.dpad_up);
            relicRaiser.setPower(-1.0);
        }
        else if(gamepad1.dpad_down) {
            //Relic riser down
            telemetry.addData("Dpad down", gamepad1.dpad_down);
            relicRaiser.setPower(1.0);
        }
        else if(gamepad1.dpad_right) {
            //Extend relic
            relicExtender.setPower(-0.5);

        }
        else if(gamepad1.dpad_left) {
            //Retract relic
            relicExtender.setPower(0.5);
        }

        else if(gamepad1.right_bumper) {
            //Switch back to front
            switchFront = true;

        }
        else if(gamepad1.left_bumper) {
            switchFront = false;
        }

        else {
            speed.stop();

            //Stock block raiser
            blockRaiser.setPower(0.0);
            relicRaiser.setPower(0.0);
            relicExtender.setPower(0.0);
        }







    }*/

    protected void hardwareSetup() {
        //Servos
        leftBlockGrabber = hardwareMap.servo.get("LBS");
        rightBlockGrabber = hardwareMap.servo.get("RBS");
        frontAngleServo = hardwareMap.servo.get("FAS");
        relicGrabberServo = hardwareMap.servo.get("ReGS");
        frontStickServo = hardwareMap.servo.get("FSS");


        //Motors
        motorFrontLeft = hardwareMap.dcMotor.get("fl");
        motorFrontRight = hardwareMap.dcMotor.get("fr");
        motorBackLeft = hardwareMap.dcMotor.get("bl");
        motorBackRight = hardwareMap.dcMotor.get("br");
        blockRaiser = hardwareMap.dcMotor.get("BlR");
        sideStickMotor = hardwareMap.dcMotor.get("SSM");
        relicExtender = hardwareMap.dcMotor.get("RE");
        relicRaiser = hardwareMap.dcMotor.get("RR");

        //Others
        myColorSensor = hardwareMap.get(ColorSensor.class, "sence");
        frontStickButton = hardwareMap.get(DigitalChannel.class, "FSB");
        frontStickButton.setMode(DigitalChannel.Mode.INPUT);
        relicRaiserMax = hardwareMap.get(DigitalChannel.class, "RRM");
        relicRaiserMax.setMode(DigitalChannel.Mode.INPUT);

    }

    private void rightTrigger(double triggerValue) {
        speed.setSpeedsFromMotorSpeeds(MotorSpeeds.getSpeed(MotionDirections.ROTATER));

        speed.backRight *= triggerValue;
        speed.backLeft *= triggerValue;
        speed.frontLeft *= triggerValue;
        speed.frontRight *= triggerValue;
        speed.updateMotors();

    }
    private void leftTrigger(double triggerValue) {
        speed.setSpeedsFromMotorSpeeds(MotorSpeeds.getSpeed(MotionDirections.ROTATEL));

        speed.backRight *= triggerValue;
        speed.backLeft *= triggerValue;
        speed.frontLeft *= triggerValue;
        speed.frontRight *= triggerValue;
        speed.updateMotors();


    }



    private void rightJoystick() {
        double fixedYValue = -gamepad1.right_stick_y;
        double xValue = gamepad1.right_stick_x;

        if(switchFront) {
            xValue *= -1;
            fixedYValue *= -1;
        }


        double rightRotation = gamepad1.right_trigger;
        double leftRotation = gamepad1.left_trigger;


        double robotSpeed = Math.sqrt(Math.pow(fixedYValue,2) + Math.pow(xValue,2));
        double changeDirectionSpeed = 0;

        if(rightRotation > leftRotation) {
            changeDirectionSpeed = rightRotation;
        }
        else {
            changeDirectionSpeed = -leftRotation;
        }





        double frontLeftPower = robotSpeed * Math.sin(Math.atan2(xValue,fixedYValue) + Math.PI/4) + changeDirectionSpeed;
        double frontRightPower = robotSpeed * Math.cos(Math.atan2(xValue,fixedYValue) + Math.PI/4) - changeDirectionSpeed;
        double backLeftPower = robotSpeed * Math.cos(Math.atan2(xValue,fixedYValue) + Math.PI/4) + changeDirectionSpeed;
        double backRightPower = robotSpeed * Math.sin(Math.atan2(xValue,fixedYValue) + Math.PI/4) - changeDirectionSpeed;



        //public MotorSpeeds(double frontL, double frontR, double backL, double backR)

        speed.setSpeeds(frontLeftPower,frontRightPower,backLeftPower,backRightPower);
        speed.frontLeft = speed.frontLeft;
        speed.frontRight = speed.frontRight;
        speed.backLeft = speed.backLeft;
        speed.backRight = speed.backRight;
        speed.updateMotors();



        /*
        //Speed is a double from 0 - 1.0 and is a scale factor to be applied to motor powers
        double speedCoef = Math.sqrt(Math.pow(gamepad1.left_stick_x,2) + Math.pow(gamepad1.left_stick_y,2));

        if(gamepad1.left_stick_x >= 0 && fixedYValue >= 0) {
            //first quad

            //If diff of values is less than .3 go diagonal
            if(Math.abs(gamepad1.left_stick_x - fixedYValue) > 0.3) {
                //Go horizontal or vertical

                //X > Y GO EAST
                if(gamepad1.left_stick_x > fixedYValue) speed.setSpeedsFromMotorSpeeds(MotorSpeeds.getSpeed(MotionDirections.E));

                    //Y > X GO NORTH
                else speed.setSpeedsFromMotorSpeeds(MotorSpeeds.getSpeed(MotionDirections.N));
            }

            else speed.setSpeedsFromMotorSpeeds(MotorSpeeds.getSpeed(MotionDirections.NE));
        }

        else if(gamepad1.left_stick_x < 0 && fixedYValue >= 0) {
            //second quad
            if(Math.abs(-gamepad1.left_stick_x - fixedYValue) > 0.3) {
                if(-gamepad1.left_stick_x > fixedYValue) {
                    //GO W
                    speed.setSpeedsFromMotorSpeeds(MotorSpeeds.getSpeed(MotionDirections.W));
                }

                else speed.setSpeedsFromMotorSpeeds(MotorSpeeds.getSpeed(MotionDirections.N));
            }
            else speed.setSpeedsFromMotorSpeeds(MotorSpeeds.getSpeed(MotionDirections.NW));

        }
        else if(gamepad1.left_stick_x < 0 && fixedYValue < 0) {
            //third quad

            if(Math.abs(gamepad1.left_stick_x - fixedYValue) > 0.3) {
                if(gamepad1.left_stick_x < fixedYValue) {
                    //GO W
                    speed.setSpeedsFromMotorSpeeds(MotorSpeeds.getSpeed(MotionDirections.W));
                }

                else speed.setSpeedsFromMotorSpeeds(MotorSpeeds.getSpeed(MotionDirections.S));
            }
            else speed.setSpeedsFromMotorSpeeds(MotorSpeeds.getSpeed(MotionDirections.SW));

        }
        else {
            //fourth quad

            if(Math.abs(gamepad1.left_stick_x + fixedYValue) > 0.3) {
                if(gamepad1.left_stick_x > -fixedYValue) {
                    //GO W
                    speed.setSpeedsFromMotorSpeeds(MotorSpeeds.getSpeed(MotionDirections.E));
                }

                else speed.setSpeedsFromMotorSpeeds(MotorSpeeds.getSpeed(MotionDirections.S));
            }
            else speed.setSpeedsFromMotorSpeeds(MotorSpeeds.getSpeed(MotionDirections.SE));

        }

        //apply speed coefficient
        speed.backRight *= speedCoef;
        speed.backLeft *= speedCoef;
        speed.frontRight *= speedCoef;
        speed.frontLeft *= speedCoef;

        speed.updateMotors();
*/

    }


    /*
    public Color getColor() {

        telemetry.addData("red: ", myColorSensor.red());
        telemetry.addData("blue: ", myColorSensor.blue());


        if (myColorSensor.red() > myColorSensor.blue()) return Color.RED;
        else return  Color.BLUE;


    }
*/






    //VuMark Detection
    public RelicRecoveryVuMark checkForVuMark() {
        RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
        if (vuMark != RelicRecoveryVuMark.UNKNOWN) {

        }

        return vuMark;
    }
}

enum Color {
    RED,
    BLUE
}


