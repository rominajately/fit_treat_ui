package code.database;



public class TableQuestion {
    static final String SQL_CREATE_QUESTION = ("CREATE TABLE question (" +
            questionColumn.id + " VARCHAR,"
            + questionColumn.question_title + " VARCHAR,"
            + questionColumn.video_url + " VARCHAR,"
            + questionColumn.thumbnail + " VARCHAR,"
            + questionColumn.question_type + " VARCHAR,"
            + questionColumn.option_type + " VARCHAR,"
            + questionColumn.answer_selection_type + " VARCHAR,"
            + questionColumn.observation_id + " VARCHAR,"
            + questionColumn.question_id + " VARCHAR,"
            + questionColumn.status + " VARCHAR" + ")");

    public static final String question = "question";

    public enum questionColumn {
        id,
        question_title,
        video_url,
        thumbnail,
        question_type,
        option_type,
        answer_selection_type,
        observation_id,
        question_id,
        status
    }
}
