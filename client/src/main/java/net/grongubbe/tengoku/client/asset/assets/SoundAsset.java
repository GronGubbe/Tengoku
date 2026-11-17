package net.grongubbe.tengoku.client.asset.assets;

import net.grongubbe.tengoku.client.asset.IAsset;

import java.util.Objects;

public class SoundAsset implements IAsset {
    private final String name;

    private final short[] samples;

    private final int sampleRate;
    private final int channelCount;

    public SoundAsset(String name, short[] samples, int sampleRate, int channelCount) {
        this.name = name;

        this.samples = Objects.requireNonNull(samples, "samples").clone();

        if (this.samples.length == 0) {
            throw new IllegalArgumentException("Samples cannot be empty");
        }

        if (sampleRate <= 0) {
            throw new IllegalArgumentException("Sample rate must be positive");
        }

        if (channelCount <= 0 || channelCount > 2) {
            throw new IllegalArgumentException("Channel count must be 1 or 2");
        }

        this.sampleRate = sampleRate;
        this.channelCount = channelCount;

        if (this.samples.length % channelCount != 0) {
            throw new IllegalArgumentException("Samples length must be divisible by channel count");
        }
    }

    public String getName() {
        return name;
    }

    public short[] getSamples() {
        return samples.clone();
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getChannelCount() {
        return channelCount;
    }
}
