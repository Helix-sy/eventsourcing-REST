package de.eventsourcingbook.cart.cosmetics.importer

import com.opencsv.CSVReaderBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Utility class for reading and validating CSV files containing cosmetics data
 */
@Component
class CosmeticsDataReader {
    private val logger = LoggerFactory.getLogger(CosmeticsDataReader::class.java)
    
    /**
     * Check if a CSV file has the required columns for cosmetics data
     * @param filePath Path to the CSV file
     * @return true if the file is valid, false otherwise
     */
    fun validateCsvFile(filePath: String): Boolean {
        val path = Paths.get(filePath)
        if (!Files.exists(path)) {
            logger.error("File not found: $filePath")
            return false
        }
        
        try {
            FileReader(filePath).use { reader ->
                val csvReader = CSVReaderBuilder(reader).build()
                val header = csvReader.readNext()
                
                // Check for required columns
                val requiredColumns = listOf("event_time", "event_type", "product_id", "category_id", "brand", "price")
                for (column in requiredColumns) {
                    if (!header.contains(column)) {
                        logger.error("Required column missing: $column")
                        return false
                    }
                }
            }
            return true
        } catch (e: Exception) {
            logger.error("Error validating CSV file", e)
            return false
        }
    }
    
    /**
     * Count the number of lines in a CSV file
     * @param filePath Path to the CSV file
     * @return Number of lines in the file (including header)
     */
    fun countLines(filePath: String): Int {
        val path = Paths.get(filePath)
        if (!Files.exists(path)) {
            return 0
        }
        
        return Files.lines(path).count().toInt()
    }
}