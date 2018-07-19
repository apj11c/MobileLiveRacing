# MobileLiveRacing
An android mobile application to race your friend in real time.

## How to play
1. Log in / Register for account.
2. Click Join Random to be queued for a game.
3. A second player will be able to join your game by entering your username
4. First player to reach the target distance wins.

## How to test
1. Open two emulators (devices).
2. Launch MobileLiveRacing App on both devices.
3. Log in to or create two seperate accounts.
4. First player clicks join random.
5. Seconds player types in email of first player and clicks join game (May need to click join random if not automatically sent to game).
6. Update the device latitude or longitude to reach the 100% distance travelled mark.
7. Click the view results button to view race results on a map.

## Known bugs
- Location permission issue on start up (discussed in class)
The location update feature will sometimes not behave correctly due to permissions. Opening Google Maps will usually fix this issue.
- App can sometimes crash if the main UI thread has too much on it.
- Location updater is not precise on emulator.
