package code.database;



public class TableOptions {
    static final String SQL_CREATE_OPTIONS = ("CREATE TABLE options (" +
            optionsColumn.id + " VARCHAR,"
            + optionsColumn.option + " VARCHAR,"
            + optionsColumn.imageUrl + " VARCHAR,"
            + optionsColumn.correctAnswer + " VARCHAR,"
            + optionsColumn.question_id + " VARCHAR,"
            + optionsColumn.answer_selection_type + " VARCHAR,"
            + optionsColumn.selected + " VARCHAR" + ")");

    public static final String options = "options";

    //ID- UUID
    //selected - 1 for selected, 0 for not selected

    public enum optionsColumn {
        id,
        option,
        imageUrl,
        correctAnswer,
        question_id,
        answer_selection_type,
        selected
    }
}
