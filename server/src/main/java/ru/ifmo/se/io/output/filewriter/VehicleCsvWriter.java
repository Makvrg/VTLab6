package ru.ifmo.se.io.output.filewriter;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import ru.ifmo.se.entity.Vehicle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collection;

@RequiredArgsConstructor
public class VehicleCsvWriter implements FileWriter<Vehicle> {

    private final String backupFileName;
    private final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @Override
    public void write(String fileName, Collection<Vehicle> vehicles)
            throws IOException {
        File file = new File(fileName);
        if (file.createNewFile() && file.canWrite()) {
            writeToFile(file, vehicles);
        } else {
            if (file.canWrite()) {
                writeToFile(file, vehicles);
            } else {
                throw new IOException();
            }
        }
    }

    @Override
    public void writeBackup(Collection<Vehicle> vehicles) throws IOException {
        write(backupFileName, vehicles);
    }

    private void writeToFile(File file, Collection<Vehicle> vehicles) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                CSVWriter writer = new CSVWriter(osw)) {

            writer.writeNext(
                    new String[]{
                            Vehicle.FieldNames.ID.getTitle(),
                            Vehicle.FieldNames.NAME.getTitle(),
                            "coordinates",
                            Vehicle.FieldNames.CREATION_DATE.getTitle(),
                            Vehicle.FieldNames.ENGINE_POWER.getTitle(),
                            Vehicle.FieldNames.DISTANCE_TRAVELLED.getTitle(),
                            Vehicle.FieldNames.TYPE.getTitle(),
                            Vehicle.FieldNames.FUEL_TYPE.getTitle()
                    }
            );

            for (Vehicle veh : vehicles) {
                String coordinates = veh.getCoordinates() != null
                        ? veh.getCoordinates().getX() + ":" + veh.getCoordinates().getY()
                        : "";
                String creationDate = veh.getCreationDate() != null
                        ? DATE_FORMAT.format(veh.getCreationDate())
                        : "";

                writer.writeNext(
                        new String[]{
                                String.valueOf(veh.getId()),
                                veh.getName() != null
                                        ? veh.getName()
                                        : "",
                                coordinates,
                                creationDate,
                                String.valueOf(veh.getEnginePower()),
                                veh.getDistanceTravelled() != null
                                        ? String.valueOf(veh.getDistanceTravelled())
                                        : "",
                                veh.getType() != null
                                        ? veh.getType().name()
                                        : "",
                                veh.getFuelType() != null
                                        ? veh.getFuelType().name()
                                        : ""
                        }
                );
            }
        }
    }
}
