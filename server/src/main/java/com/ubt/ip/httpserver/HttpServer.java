package com.ubt.ip.httpserver;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ubt.ip.ctrl_motor.util.LedUtil;
import com.ubt.ip.ctrl_motor.util.MotorUtil;
import com.ubt.ip.httpserver.bean.LedBean;
import com.ubt.ip.httpserver.bean.MotorBean;
import com.ubt.ip.httpserver.mock.Robot;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by afunx on 15/09/2017.
 */

class HttpServer extends NanoHTTPD {

    private Robot mRobot = new Robot();
    private static final String TAG = "HttpServer";

    private Gson gson;

    HttpServer(int port) {
        super(port);
        gson = new GsonBuilder().create();
    }

    public HttpServer(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Log.i(TAG, "serve() uri: " + session.getUri());
        if (session.getUri().equals("/motors") && session.getMethod() == Method.GET) {
            return serveGetMotors(session);
        } else if (session.getUri().equals("/motor") && session.getMethod() == Method.POST) {
            return servePostMotor(session);
        } else if (session.getUri().equals("/motors") && session.getMethod() == Method.POST) {
            return servePostMotors(session);
        } else if (session.getUri().equals("/cancel/motor") && session.getMethod() == Method.POST) {
            return serveCancelOperationMotor(session);
        } else if (session.getUri().equals("/readmode/enter") && session.getMethod() == Method.POST) {
            return serveEnterReadMode(session);
        } else if (session.getUri().equals("/readmode/exit") && session.getMethod() == Method.POST) {
            return serveExitReadMode(session);
        } else if (session.getUri().equals("/readmode/check") && session.getMethod() == Method.GET) {
            return serveCheckReadMode(session);
        } else if (session.getUri().equals("/led") && session.getMethod() == Method.POST) {
            return servePostLed(session);
        }
        return newFixedLengthResponse(Response.Status.BAD_REQUEST, NanoHTTPD.MIME_PLAINTEXT, "URL: " + session.getUri() + " IS BAD REQUEST");
    }

    private Response servePostMotors(IHTTPSession session) {
        Log.i(TAG, "servePostMotors()");
        // 从Client获取motor bean List
        Map<String, String> files = new HashMap<String, String>();
        try {
            session.parseBody(files);
            List<MotorBean> motorBeanList = gson.fromJson(files.get("postData"), new TypeToken<List<MotorBean>>() {
            }.getType());
            if (motorBeanList.size() > 0) {
                Log.d(TAG, "servePostMotors() motorBeanList.get(0).getMotorId()" + motorBeanList.get(0).getMotorId());
                Log.d(TAG, "servePostMotors() motorBeanList.get(0).getMotorAbsoluteDegree()" + motorBeanList.get(0).getMotorAbsoluteDegree());
                Log.d(TAG, "servePostMotors() motorBeanList.get(0).getMotorDelayMilli()" + motorBeanList.get(0).getMotorDelayMilli());
                Log.d(TAG, "servePostMotors() motorBeanList.get(0).getMotorRunMilli()" + motorBeanList.get(0).getMotorRunMilli());
            }
            // 在Robot设置Motors

            // 组装Response
            int[] motorsId = new int[motorBeanList.size()];
            int[] motorsDegree = new int[motorBeanList.size()];
            for (int i = 0; i < motorsId.length; i++) {
                motorsId[i] = motorBeanList.get(i).getMotorId();
                motorsDegree[i] = motorBeanList.get(i).getMotorAbsoluteDegree();
            }
            // TODO
            int[] taskId = new int[1];
            long runTimeMilli = motorBeanList.get(0).getMotorRunMilli();
            long delayMilli = motorBeanList.get(0).getMotorDelayMilli();
            MotorUtil.setMotors(motorsId, motorsDegree, runTimeMilli, delayMilli, taskId, null);
            mRobot.setMotorsDegree(motorsId, motorsDegree);
            String response = gson.toJson(taskId[0]);
            return newFixedLengthResponse(Response.Status.OK, "text/json", response);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
        } catch (ResponseException re) {
            re.printStackTrace();
            return newFixedLengthResponse(re.getStatus(), NanoHTTPD.MIME_PLAINTEXT, re.getMessage());
        }
    }

    private Response servePostMotor(IHTTPSession session) {
        Log.i(TAG, "servePostMotor()");
        // 从Client获取motor bean
        Map<String, String> files = new HashMap<String, String>();
        try {
            session.parseBody(files);
            MotorBean motorBean = gson.fromJson(files.get("postData"), MotorBean.class);
            Log.d(TAG, "servePostMotor() motorBean.getMotorId(): " + motorBean.getMotorId());
            Log.d(TAG, "servePostMotor() motorBean.getMotorAbsoluteDegree(): " + motorBean.getMotorAbsoluteDegree());
            Log.d(TAG, "servePostMotor() motorBean.getMotorDelayMilli(): " + motorBean.getMotorDelayMilli());
            Log.d(TAG, "servePostMotor() motorBean.getMotorRunMilli(): " + motorBean.getMotorRunMilli());
            // 在Robot设置Motor
            // TODO
            int[] taskId = new int[1];
            MotorUtil.setMotor(motorBean.getMotorId(), motorBean.getMotorAbsoluteDegree(),
                    motorBean.getMotorRunMilli(), motorBean.getMotorDelayMilli(), taskId, null);
            // 组装Response
            mRobot.setMotorDegree(motorBean.getMotorId(), motorBean.getMotorAbsoluteDegree());
            String response = gson.toJson(taskId[0]);
            return newFixedLengthResponse(Response.Status.OK, "text/json", response);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
        } catch (ResponseException re) {
            re.printStackTrace();
            return newFixedLengthResponse(re.getStatus(), NanoHTTPD.MIME_PLAINTEXT, re.getMessage());
        }
    }

    private Response serveGetMotors(IHTTPSession session) {
        Log.i(TAG, "serveGetMotors()");
        // 从Client获取motor ids
        Map<String, List<String>> parameters = session.getParameters();
        List<String> idsStr = parameters.get("id");
        int[] ids = new int[idsStr.size()];
        for (int i = 0; i < idsStr.size(); i++) {
            ids[i] = Integer.parseInt(idsStr.get(i));
        }
        Log.d(TAG, "serveGetMotors() ids: " + Arrays.toString(ids));
        // 从Robot获取motor degrees
        int[] degrees = mRobot.getMotorsDegree(ids);
        // TODO
        MotorUtil.getCurrentMotors(ids, degrees);
        Log.e("afunx", "HttpServer getTargetMotors: " + Arrays.toString(degrees));
        // 组装response
        String response = gson.toJson(degrees);
        return newFixedLengthResponse(Response.Status.OK, "text/json", response);
    }

    private Response serveCancelOperationMotor(IHTTPSession session) {
        Log.i(TAG, "serveCancelOperationMotor()");
        // 从Client获取op id
        Map<String, List<String>> parameters = session.getParameters();
        List<String> idsStr = parameters.get("id");
        int opId = Integer.parseInt(idsStr.get(0));
        Log.d(TAG, "serveCancelOperationMotor() opId: " + opId);
        // 从Robot获取cancel结果
        // TODO
        // 组装response
        boolean isSuc = true;
        String response = gson.toJson(isSuc);
        return newFixedLengthResponse(Response.Status.OK, "text/json", response);
    }

    private Response serveEnterReadMode(IHTTPSession session) {
        Log.i(TAG, "serveEnterReadMode()");
        // 从Robot获取enterReadMode结果
        // TODO
        // 组装response
        mRobot.enterReadMode();
        boolean isSuc[] = new boolean[1];
        MotorUtil.enterReadMode(isSuc, new int[]{1, 2, 3, 4, 5, 6, 7});
        String response = gson.toJson(isSuc[0]);
        return newFixedLengthResponse(Response.Status.OK, "text/json", response);
    }

    private Response serveExitReadMode(IHTTPSession session) {
        Log.i(TAG, "serveExitReadMode()");
        // 从Robot获取exitReadMode结果
        // TODO
        // 组装response
        mRobot.exitReadMode();
        boolean isSuc[] = new boolean[1];
        MotorUtil.exitReadMode(isSuc, new int[]{1, 2, 3, 4, 5, 6, 7});
        String response = gson.toJson(isSuc[0]);
        return newFixedLengthResponse(Response.Status.OK, "text/json", response);
    }

    private Response serveCheckReadMode(IHTTPSession session) {
        Log.i(TAG, "serveCheckReadMode()");
        // 从Robot获取checkReadMode结果
        // TODO
        // 组装response
        int readMode = mRobot.getReadMode();
        String response = gson.toJson(readMode);
        return newFixedLengthResponse(Response.Status.OK, "text/json", response);
    }

    private Response servePostLed(IHTTPSession session) {
        Log.i(TAG, "servePostLed()");
        // 从Client获取led bean
        Map<String, String> files = new HashMap<String, String>();
        try {
            session.parseBody(files);
            LedBean ledBean = gson.fromJson(files.get("postData"), LedBean.class);
            Log.d(TAG, "servePostMotor() LedBean.getLedId(): " + ledBean.getLedId());
            Log.d(TAG, "servePostMotor() LedBean.getLedColor(): " + ledBean.getLedColor());
            Log.d(TAG, "servePostMotor() LedBean.getLedFrequency(): " + ledBean.getLedFrequency());
            // 在Robot设置Led
            // TODO
            LedUtil.setLed(ledBean.getLedId(),ledBean.getLedColor(),ledBean.getLedFrequency());
            String response = "";
            return newFixedLengthResponse(Response.Status.OK, "text/json", response);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
        } catch (ResponseException re) {
            re.printStackTrace();
            return newFixedLengthResponse(re.getStatus(), NanoHTTPD.MIME_PLAINTEXT, re.getMessage());
        }
    }

}