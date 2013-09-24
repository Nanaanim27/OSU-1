package edu.osu.cse.misc.audio.organization;

import java.io.File;
import java.io.FileFilter;

/**
 * A simple utility that organizes a music folder into directories specific to the author of a song.
 * <pre>
 * 
 * Turns this folder structure:
 * MUSIC_DIR
 * ---Song
 * ---Song
 * ---Song
 * 
 * 
 * into to the following structure:
 * 
 * MUSIC_DIR
 * ---Artist
 * ------Song
 * ------Song
 * ---Artist
 * ------Song
 * 
 *  
 * </pre>
 */
public class OragnizeMusic {

	private static final String MUSIC_DIR = "C:\\Users\\Matt\\Desktop\\Music";
	private static Mode mode = Mode.COMPRESS;
	private static FileFilter filter = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.getAbsolutePath().endsWith(".mp3");
		}
	};

	public static void main(String[] args) {
		File musicDir = new File(MUSIC_DIR);
		if (musicDir.exists()) {
			for (File musicDirFile : mode == Mode.COMPRESS ? musicDir.listFiles(filter) : musicDir.listFiles()) {
				if (mode == Mode.COMPRESS) {
					if (!musicDirFile.isDirectory()) {
						String fileName = musicDirFile.getName();
						String[] elements = fileName.split("[-�]");
						try {
							String artist = elements[0].trim(), songName = elements[1].trim();
							File artistDirectory = new File(MUSIC_DIR, artist);

							if (artistDirectory.exists() || artistDirectory.mkdir())
								musicDirFile.renameTo(new File(artistDirectory, songName));

						} catch (ArrayIndexOutOfBoundsException badFile) {
							System.out.println(badFile.getMessage());
							continue;
						}
					}
				}
				else {
					if (musicDirFile.isDirectory()) {
						for (File song : musicDirFile.listFiles(filter)) {
							String artist = musicDirFile.getName();
							if (!song.renameTo(new File(MUSIC_DIR, (artist + " - " + song.getName())))) {
								System.err.println("Failed to expand " + song.getName());
							}
						}
						if (musicDirFile.listFiles().length == 0) {
							musicDirFile.delete();
						}
						else {
							System.err.println(musicDirFile.getName() + " is not empty. Cannot delete.");
						}
					}
				}
			}
		}
		else {
			System.err.println("Music Directory does not exist: " + MUSIC_DIR);
		}
	}

	private enum Mode {
		COMPRESS,
		EXPAND
	}

}
