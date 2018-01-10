package mod.manager.constant;

/**
 * 狀態
 */
public enum Status {

	OK("OK", "一切正常"),
	MISS("MISS", "MOD檔案遺失"),
	LOST("LOST", "遊戲MOD資料遺失"),
	NEWVER("NEWVER", "MOD檔案較遊戲檔案新"),
	OLDVER("OLDVER", "MOD檔案較遊戲檔案舊");
	
	public String label;
	public String hint;
	
	private Status(String label, String hint) {
		this.label = label;
		this.hint = hint;
	}
}
