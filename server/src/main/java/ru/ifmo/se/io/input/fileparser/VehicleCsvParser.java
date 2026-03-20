package ru.ifmo.se.io.input.fileparser;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import ru.ifmo.se.entity.Vehicle;
import ru.ifmo.se.io.output.formatter.StringFormatter;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class VehicleCsvParser implements FileParser<Vehicle> {

    private final StringFormatter formatter;

    @Override
    public List<Vehicle> parse(InputStreamReader reader) {
        Scanner scanner = new Scanner(reader);
        scanner.useDelimiter("\\A");
        String content = scanner.hasNext() ? scanner.next() : "";
        scanner.close();

        try (StringReader sr = new StringReader(content)) {
            CsvToBean<Vehicle> csvToBean = makeCsvToBean(sr);

            List<Vehicle> vehicles = csvToBean.parse();
            List<CsvException> exceptions = csvToBean.getCapturedExceptions();

            if (!exceptions.isEmpty()) {
                throw new CsvValidationException(
                        formatter.formatFileVehiclesExc(exceptions));
            }
            return vehicles;
        } catch (CsvValidationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new CsvValidationException(
                    "Ошибка чтения CSV. Возможно, у файла неправильная структура", e);
        }
    }

    private CsvToBean<Vehicle> makeCsvToBean(StringReader stringReader) {
        HeaderColumnNameMappingStrategy<Vehicle> strategy =
                new HeaderColumnNameMappingStrategy<>();
        strategy.setType(Vehicle.class);

        return new CsvToBeanBuilder<Vehicle>(stringReader)
                .withMappingStrategy(strategy)
                .withType(Vehicle.class)
                .withIgnoreLeadingWhiteSpace(true)
                .withThrowExceptions(false)
                .build();
    }
}
