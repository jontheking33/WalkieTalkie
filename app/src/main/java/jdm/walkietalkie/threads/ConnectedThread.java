package jdm.walkietalkie.threads;

import android.bluetooth.BluetoothSocket;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jdm.walkietalkie.MainActivity;

/**
 * Created by tmast_000 on 6/13/2015.
 */
public class ConnectedThread extends Thread{

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private static final int RECORDER_SAMPLERATE = 8000;
        private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
        private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
        private boolean isRecording = false;
        private int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };
        int bufferSize;

        private final int RECEIVE_AUDIO = 1;


        public ConnectedThread(BluetoothSocket socket) {
            this.mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;


            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            // buffer store for the stream
            int bytes; // bytes returned from read()
            int BytesPerElement = 2; // 2 bytes in 16bit format

            int data = 0;
            byte[] buffer = new byte[1024];

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                // Read from the InputStream
                try {
                    buffer = new byte[1024];
                    data = mmInStream.read(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Send the obtained bytes to the UI activity
                if(MainActivity.getHandler() != null) {
                    MainActivity.getHandler().obtainMessage(RECEIVE_AUDIO, data, -1, buffer).sendToTarget();
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    public void getRecordingThread() {
        RecordingThread recordingThread = new RecordingThread(this);
        recordingThread.start();
    }
}

