package testpracticsample.eim.systems.cs.pub.ro.testpracticsample.Network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.HttpResponseException;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import testpracticsample.eim.systems.cs.pub.ro.testpracticsample.Utilities.Constants;
import testpracticsample.eim.systems.cs.pub.ro.testpracticsample.Utilities.Utilities;

public class ComunicationThread extends Thread {
        private ServerThread serverThread;
        private Socket socket;

        public ComunicationThread(ServerThread serverThread, Socket socket) {
            this.serverThread = serverThread;
            this.socket = socket;
        }

        @Override
        public void run() {
            if (socket == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
                return;
            }
            try {
                BufferedReader bufferedReader = Utilities.getReader(socket);
                PrintWriter printWriter = Utilities.getWriter(socket);
                if (bufferedReader == null || printWriter == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                    return;
                }
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");
                String city = bufferedReader.readLine();
                System.out.println("[------------------]" + city);
                HttpURLConnection httpURLConnection = null;
                String data = serverThread.getData();

                String weatherForecastInformation = null;

                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();

                String str = city;//"https://autocomplete.wunderground.com/aq?query=" + city
                HttpGet httpGet = new HttpGet(str);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageSourceCode = null;

                try {
                    pageSourceCode = httpClient.execute(httpGet, responseHandler);
                    if (pageSourceCode == null) {
                        Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                        return;
                    }
                    System.out.println(pageSourceCode);
                    printWriter.println(pageSourceCode);
                   printWriter.flush();
                } catch (HttpResponseException httpResponseException) {
                    Log.e(Constants.TAG, httpResponseException.getMessage());
                    if (Constants.DEBUG) {
                        printWriter.println("Bad URL");
                        printWriter.flush();
                        httpResponseException.printStackTrace();
                    }
                } catch (ClientProtocolException clientProtocolException) {
                    Log.e(Constants.TAG, clientProtocolException.getMessage());
                    if (Constants.DEBUG) {
                        clientProtocolException.printStackTrace();
                    }
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }


                /*
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageSourceCode = httpClient.execute(httpGet, responseHandler);

                String dataNew = null;
                StringBuffer strBuffer = new StringBuffer();
                String[] token = pageSourceCode.split("name");
                for (int i=1; i< token.length; i++){
                    String[] tok = token[i].split(",");
                    strBuffer.append(tok[0].replace("\"",""));
                    strBuffer.append("\n");
                    dataNew = dataNew + "\n"+ tok[0].replace("\"","");
                }

                System.out.println("=+++++++++ "+dataNew);

                String result = dataNew;
                printWriter.println(result);
                printWriter.flush();
                */
                } catch (MalformedURLException malformedURLException) {
                    Log.e(Constants.TAG, malformedURLException.getMessage());
                    if (Constants.DEBUG) {
                        malformedURLException.printStackTrace();
                    }
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            } catch (Exception exception) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + exception.getMessage());
                if (Constants.DEBUG) {
                    exception.printStackTrace();
                }
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ioException) {
                        Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                        if (Constants.DEBUG) {
                            ioException.printStackTrace();
                        }
                    }
                }
            }
        }
    }
