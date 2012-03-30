package myorg.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import javax.print.PrintService;
import javax.print.attribute.PrintRequestAttributeSet;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.commons.lang.StringUtils;

import pt.utl.ist.fenix.tools.util.PropertiesManager;

public class ReportUtils extends PropertiesManager {

    static final private Map<String, JasperReport> reportsMap = new ConcurrentHashMap<String, JasperReport>();

    static final private Properties properties = new Properties();

    static final private String reportsPropertiesFile = "/reports.properties";

    static {
	try {
	    loadProperties(properties, reportsPropertiesFile);
	} catch (IOException e) {
	    throw new RuntimeException("Unable to load properties files.", e);
	}
    }

    static public boolean exportToPdfFile(String key, Map parameters, ResourceBundle bundle, Collection dataSource,
	    String destination) {
	try {
	    final JasperPrint jasperPrint = createJasperPrint(key, parameters, bundle, dataSource);
	    if (jasperPrint != null) {
		JasperExportManager.exportReportToPdfFile(jasperPrint, destination);
		return true;
	    } else {
		return false;
	    }
	} catch (JRException e) {
	    return false;
	}
    }

    static public byte[] exportToPdfFileAsByteArray(final String key, final Map parameters, final ResourceBundle bundle,
	    final Collection dataSource) throws JRException {
	final JasperPrint jasperPrint = createJasperPrint(key, parameters, bundle, dataSource);

	if (jasperPrint != null) {
	    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    export(new JRPdfExporter(), Collections.singletonList(jasperPrint), baos, (PrintService) null,
		    (PrintRequestAttributeSet) null);
	    return baos.toByteArray();
	}

	return null;
    }

    static private JasperPrint createJasperPrint(final String key, final Map parameters, final ResourceBundle bundle,
	    Collection dataSource) throws JRException {
	JasperReport report = reportsMap.get(key);

	if (report == null) {
	    final String reportFileName = properties.getProperty(key);
	    if (reportFileName != null) {
		report = (JasperReport) JRLoader.loadObject(ReportUtils.class.getResourceAsStream(reportFileName));
		reportsMap.put(key, report);
	    }
	}

	if (report != null) {
	    if (parameters != null && bundle != null) {
		parameters.put(JRParameter.REPORT_RESOURCE_BUNDLE, bundle);
	    }

	    if (dataSource == null || dataSource.isEmpty()) {
		// dummy, engine seems to work not very well with empty data
		// sources
		dataSource = Collections.singletonList(StringUtils.EMPTY);
	    }

	    return JasperFillManager.fillReport(report, parameters, new JRBeanCollectionDataSource(dataSource));
	}

	return null;
    }

    static private void export(final JRAbstractExporter exporter, final List<JasperPrint> prints,
	    final ByteArrayOutputStream stream, final PrintService printService,
	    final PrintRequestAttributeSet printRequestAttributeSet) throws JRException {

	if (prints.size() == 1) {
	    exporter.setParameter(JRExporterParameter.JASPER_PRINT, prints.iterator().next());
	} else {
	    exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, prints);
	}

	if (stream != null) {
	    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, stream);
	}

	if (printService != null) {
	    exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, printService);
	}

	if (printRequestAttributeSet != null) {
	    exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
	}

	exporter.exportReport();
	return;
    }

}
