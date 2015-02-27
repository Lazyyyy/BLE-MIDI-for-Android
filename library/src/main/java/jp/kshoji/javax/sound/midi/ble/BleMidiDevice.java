package jp.kshoji.javax.sound.midi.ble;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.kshoji.blemidi.device.MidiInputDevice;
import jp.kshoji.blemidi.device.MidiOutputDevice;
import jp.kshoji.javax.sound.midi.MidiDevice;
import jp.kshoji.javax.sound.midi.MidiUnavailableException;
import jp.kshoji.javax.sound.midi.Receiver;
import jp.kshoji.javax.sound.midi.Transmitter;

/**
 * {@link jp.kshoji.javax.sound.midi.MidiDevice} implementation
 *
 * @author K.Shoji
 */
public final class BleMidiDevice implements MidiDevice {
    private BleMidiReceiver receiver;
    private BleMidiTransmitter transmitter;

    private boolean isOpened;

    private MidiInputDevice midiInputDevice;
    private MidiOutputDevice midiOutputDevice;

    /**
     * Constructor
     *
     * @param midiInputDevice the input device
     * @param midiOutputDevice the output device
     */
    public BleMidiDevice(@Nullable MidiInputDevice midiInputDevice, @Nullable MidiOutputDevice midiOutputDevice) {
        this.midiInputDevice = midiInputDevice;
        this.midiOutputDevice = midiOutputDevice;

        if (midiInputDevice == null && midiOutputDevice == null) {
            throw new NullPointerException("Both of MidiInputDevice and MidiOutputDevice are null.");
        }

        if (midiOutputDevice != null) {
            receiver = new BleMidiReceiver(this);
        }

        if (midiInputDevice != null) {
            transmitter = new BleMidiTransmitter(this);
        }
    }

    @NonNull
    @Override
    public Info getDeviceInfo() {
        String deviceName = "";
        if (midiInputDevice != null) {
            deviceName = midiInputDevice.getDeviceName();
        } else if (midiOutputDevice != null) {
            deviceName = midiOutputDevice.getDeviceName();
        }

        return new Info(deviceName, //
                "(vendor)", //
                "(description)", //
                "(version)");
    }

    @Override
    public void open() throws MidiUnavailableException {
        if (isOpened) {
            return;
        }

        if (receiver != null) {
            receiver.open();
        }

        if (transmitter != null) {
            transmitter.open();
        }

        isOpened = true;
    }

    @Override
    public void close() {
        if (!isOpened) {
            return;
        }

        if (transmitter != null) {
            transmitter.close();
            transmitter = null;
        }

        if (receiver != null) {
            receiver.close();
            receiver = null;
        }

        isOpened = false;
    }

    @Override
    public boolean isOpen() {
        return isOpened;
    }

    @Override
    public long getMicrosecondPosition() {
        // time-stamping is not supported
        return -1;
    }

    @Override
    public int getMaxReceivers() {
        return receiver == null ? 0 : 1;
    }

    @Override
    public int getMaxTransmitters() {
        return transmitter == null ? 0 : 1;
    }

    @Override
    public Receiver getReceiver() throws MidiUnavailableException {
        return receiver;
    }

    @NonNull
    @Override
    public List<Receiver> getReceivers() {
        ArrayList<Receiver> receivers = new ArrayList<>();
        if (receiver != null) {
            receivers.add(receiver);
        }
        return Collections.unmodifiableList(receivers);
    }

    @Override
    public Transmitter getTransmitter() throws MidiUnavailableException {
        return transmitter;
    }

    @NonNull
    @Override
    public List<Transmitter> getTransmitters() {
        ArrayList<Transmitter> transmitters = new ArrayList<>();
        if (transmitter != null) {
            transmitters.add(transmitter);
        }
        return Collections.unmodifiableList(transmitters);
    }

    public void setMidiInputDevice(@Nullable MidiInputDevice midiInputDevice) {
        this.midiInputDevice = midiInputDevice;
        if (transmitter != null) {
            transmitter.close();
        }

        if (midiInputDevice == null) {
            transmitter = null;
        } else {
            transmitter = new BleMidiTransmitter(this);
        }
    }

    public MidiInputDevice getMidiInputDevice() {
        return midiInputDevice;
    }

    public void setMidiOutputDevice(@Nullable MidiOutputDevice midiOutputDevice) {
        this.midiOutputDevice = midiOutputDevice;
        if (receiver != null) {
            receiver.close();
        }

        if (this.midiOutputDevice == null) {
            receiver = null;
        } else {
            receiver = new BleMidiReceiver(this);
        }
    }

    public MidiOutputDevice getMidiOutputDevice() {
        return midiOutputDevice;
    }

}
