package application.utils.pdf;

import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import javafx.scene.control.TableColumn;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PdfExportOptions<T>
{

	// ================= Layout =================
	public Rectangle pageSizeQuer = PageSize.A4.rotate();
	public Rectangle pageSize = PageSize.A4;
	public float marginLeft = 20;
	public float marginRight = 20;
	public float marginTop = 40;
	public float marginBottom = 20;
	public boolean mitTabellenkopf = true;
	public boolean mitDetailtabelle = true;
	public boolean mitZwischentext = false;

	// ================= Fonts =================
	public float headerFontSize = 12;
	public float headerFontSize2 = 10;
	public float cellFontSize = 10;
	public float cellFontSizeklein = 8;
	public float titleFontSize = 14;
	public float groupFontSize = 12f;
	public float groupHeaderColumnFontSize = 8f;

	// ================= Farben =================
	public Color headerBackground = new Color(255, 253, 208);
	public Color zebraColor = new Color(235, 235, 235);
	public Color groupHeaderBackground = new Color(255, 255, 255);

	// ================= Verhalten =================
	public boolean zebra = true;
	public boolean showPageNumbers = true;
	public boolean autoWrap = true;
	public boolean showPageDate = true;
	public boolean repeatHeaderPerGroup=false;
	

	// ================= Text =================
	public String title = "";
	public String subtitle = "";

	// ================= Spalten =================
	//public Set<TableColumn<?, ?>> excludedColumnsalt = new HashSet<>();
    public List<TableColumn<T, ?>> excludedColumns = new ArrayList<>();
	public Map<Object, Integer> columnAlignment = new HashMap<>();
	public float[] columnWidths;

	// ================= Gruppierung =================
	public TableColumn<T, ?> groupByColumn;
	
	public String groupHeaderPrefix = "gruppiert";
    // 🔥 NEU hinzufügen:
    public TableColumn<T, ?> defaultGroupByColumn;
    public String defaultGroupHeaderPrefix;


	// ================= Detailzeilen =================
	public Function<T, String> detailLine1Extractor;
	public Function<T, String> detailLine2Extractor;
	public Function<T, String> detailLine3Extractor;

	public Function<T, String> detailImageExtractor; // Bildpfad
	public Function<T, String> detailImageTextExtractor; // Text neben Bild

	public Function<T, String> detailRemarkExtractor; // Abschlusszeile

	public Function<T, Boolean> rowFilter;

	// ==================== Default-Optionen ====================
	public static <T> PdfExportOptions<T> defaults()
	{

		return new PdfExportOptions<>();
	}

	/**
	 * Spezielle Default-Optionen für Ablaufplan (AktionenListePositionenModel) Die
	 * Lambdas für Detailzeilen werden hier NICHT gesetzt, um Generics sauber zu
	 * halten. Sie können später im Controller gesetzt werden.
	 */
	public static <T> PdfExportOptions<T> defaultsAblaufplan()
	{

		PdfExportOptions<T> opt = new PdfExportOptions<>();
		opt.title = "Ablaufplan";
		opt.subtitle = "";
		opt.columnWidths = new float[] { 1, 1, 1, 1, 1, 1, 1 };

		opt.titleFontSize = 12;
		opt.headerFontSize = 9;
		opt.cellFontSize = 9;

		opt.zebra = false;
		opt.autoWrap = true;
		opt.headerBackground = new Color(255, 255, 250);
		opt.repeatHeaderPerGroup = false; // default: false
		return opt;
	}

	public static <T> PdfExportOptions<T> defaultsLiteraturlisten()
	{
		PdfExportOptions<T> opt = new PdfExportOptions<>();
		opt.pageSize = PageSize.A4.rotate();
		opt.headerFontSize = 9;
		opt.cellFontSize = 8;
		opt.zebra = true;
		opt.repeatHeaderPerGroup = true; // default: false
		return opt;
	}

	public static <T> PdfExportOptions<T> defaultsCompact()
	{
		PdfExportOptions<T> opt = new PdfExportOptions<>();
		opt.cellFontSize = 7;
		opt.headerFontSize = 8;
		opt.marginLeft = 5;
		opt.marginRight = 5;
		return opt;
	}

}