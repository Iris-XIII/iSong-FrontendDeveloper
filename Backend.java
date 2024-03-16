// == CS400 Spring 2024 File Header Information ==
// Name: Charlie Harrington
// Email: ctharrington@wisc.edu
// Lecturer: Dahl LEC 001
// Notes to Grader:

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

public class Backend implements BackendInterface {

  public Backend(IterableRedBlackTree<SongInterface> tree) {}
  ArrayList<SongInterface> songs = new ArrayList();

  ArrayList<SongInterface> rangedSongList = new ArrayList<>();
  List<List<String>> csvData = new ArrayList<>();

  private int minEnergy = 0;

  private int high = Integer.MAX_VALUE;

  private int low = 0;


  private void parseAndAddToCsvData(String line) {

//    StringBuilder currentValue = new StringBuilder();
//    boolean inQuotes = false;
//
//    for (int i = 0; i < line.length(); i++) {
//      char c = line.charAt(i);
//      if (c == '"') {
//        if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
//          // Double quote inside quoted field
//          currentValue.append('"');
//          i++; // Skip next quote
//        } else {
//          // Toggle inQuotes flag
//          inQuotes = !inQuotes;
//        }
//      } else if (c == ',' && !inQuotes) {
//        // Comma outside quotes, add current value to list
//        values.add(currentValue.toString());
//        currentValue.setLength(0); // Reset current value
//      } else {
//        // Add character to current value
//        currentValue.append(c);
//      }
//    }
//
//    // Add last value to list
//    values.add(currentValue.toString());
//
//    // Add the parsed values to csvData
//    csvData.add(values);
  }

//  public List<List<String>> getCsvData() {
//    return csvData;
//  }


  @Override
  public void readData(String filename) throws IOException {
    try (Scanner scan = new Scanner(new File(filename))) {
      scan.nextLine(); // Read the header and discard it

      while (scan.hasNextLine()) {
        String line = scan.nextLine();
        List<String> parts = splitCSVLine(line);
        if (parts.size() == 14) {
          SongInterface tempSong = new Song(
              parts.get(0), parts.get(1), parts.get(2),
              Integer.parseInt(parts.get(3)),
              Integer.parseInt(parts.get(4)),
              Integer.parseInt(parts.get(5)),
              Integer.parseInt(parts.get(6)),
              Integer.parseInt(parts.get(7)),
              Integer.parseInt(parts.get(8))
          );
          songs.add(tempSong);
        }
      }
    }
  }

  private List<String> splitCSVLine(String line) {
    List<String> parts = new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    boolean inQuotes = false;

    for (char c : line.toCharArray()) {
      if (c == '"') {
        inQuotes = !inQuotes;
      } else if (c == ',' && !inQuotes) {
        parts.add(sb.toString());
        sb.setLength(0); // Clear the StringBuilder
      } else {
        sb.append(c);
      }
    }

    // Add the last part
    parts.add(sb.toString());

    return parts;
  }
//    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
//      String line;
//      while ((line = reader.readLine()) != null) {
//        parseAndAddToCsvData(line);
//      }
//    }


  @Override
  public List<String> getRange(int low, int high) {
    rangedSongList.clear();
    this.low = low;
    this.high = high;
    ArrayList<String> rangedSongs = new ArrayList<>();
    for (int i = 0; i < songs.size() ; i++) {
      if (songs.get(i).getDanceability() >= low && songs.get(i).getDanceability() <= high &&
          songs.get(i).getEnergy() >= minEnergy) {
        rangedSongList.add(songs.get(i));
      }
    }
    Collections.sort(rangedSongList);
    for (SongInterface s: rangedSongList){
      rangedSongs.add(s.getTitle());
    }
    return rangedSongs;
  }

  @Override
  public List<String> filterEnergeticSongs(int minEnergy) {
    this.minEnergy = minEnergy;
    ArrayList<String> filteredSongs = new ArrayList<>();
    if (rangedSongList.isEmpty()) {
      return filteredSongs;
    }

    for (int i = 0; i < rangedSongList.size(); i++) {
      if (rangedSongList.get(i).getEnergy() >= minEnergy) {
        filteredSongs.add(rangedSongList.get(i).getTitle());
      }
    }
    return filteredSongs;
  }

  @Override
  public List<String> fiveFastest() throws IllegalStateException {
    if (rangedSongList.isEmpty()) {
      throw new IllegalStateException();
    }
    getRange(this.low, this.high);
    Collections.sort(rangedSongList, Comparator.comparingInt(SongInterface::getBPM).reversed());
    ArrayList<SongInterface> fiveFast = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      fiveFast.add(rangedSongList.get(i));
    }
    Collections.sort(fiveFast, Comparator.comparingInt(SongInterface::getDanceability).reversed());
    Set<String> uniqueSongs = new LinkedHashSet<>();
    for (int i = 0; i < fiveFast.size(); i++) {
      SongInterface song = fiveFast.get(i);
      uniqueSongs.add(song.getBPM() + ": " + song.getTitle());
      if (uniqueSongs.size() >= 5) {
        break; // Stop adding elements once the set size reaches 5
      }
    }

    // If there are more than 5 unique songs, remove elements from the end
    while (uniqueSongs.size() > 5) {
      uniqueSongs.remove(uniqueSongs.toArray()[uniqueSongs.size() - 1]);
    }
    return new ArrayList<>(uniqueSongs);
  }

  /**
   * Private helper to sort list by BPM
   * @return list of songs
   */


  @Override
  public String toString() {
    StringBuilder empty = new StringBuilder();
    for (int i = 0; i < songs.size(); i++) {
      empty.append(songs.get(i).getTitle()).append("\n");
    }
    return empty.toString();
  }
}
