# DSSS Lab Instructions

## Overview
This lab demonstrates Direct Sequence Spread Spectrum (DSSS) with channel noise simulation in Java.

### Files:
- `DSSSSender.java`: Spreads input text using a chip code, simulates noise, writes spread and noisy signals.
- `DSSSReceiver.java`: Despreads the signal, reconstructs the original message.

## Usage
1. **Compile:**
   javac DSSSSender.java DSSSReceiver.java

2. **Run Sender:**
   java DSSSSender input1.txt 101 0.1
   - `input1.txt`: Input file
   - `101`: Chip code (as a string of 0s and 1s)
   - `0.1`: Noise probability (e.g., 0.1 for 10%)

   Output: `spread_input1.txt`, `noisy_input1.txt`

3. **Run Receiver:**
   java DSSSReceiver noisy_input1.txt 101
   - `noisy_input1.txt`: Noisy or spread file
   - `101`: Same chip code

   Output: `recovered_input1.txt`

## Notes
- The programs print/log the original, spread, noisy, and recovered messages for verification.
- You can use any chip code (e.g., 101, 110, etc.).
- Try different noise levels to observe DSSS fault tolerance.

## Example
Input: `A` in `input1.txt`, chip code `101`, noise `0.1`
- Spread: 1 0 1 0 1 0 1 0 1 1 0 1 ...
- Noisy: (some bits flipped)
- Recovered: `A`

## Questions for Analysis
- Does redundancy increase tolerance?
- Does a longer chip code improve immunity?

---
