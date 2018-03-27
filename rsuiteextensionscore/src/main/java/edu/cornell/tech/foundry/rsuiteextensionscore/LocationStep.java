package edu.cornell.tech.foundry.rsuiteextensionscore;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.step.QuestionStep;

/**
 * Created by Christina on 6/20/17.
 */

public class LocationStep extends QuestionStep {

    @Override
    public Class getStepLayoutClass() {
        return LocationStepLayout.class;
    }


    /** Returns a new question step that includes the specified identifier, title, question and
     * answer format
     * @param identifier The identifier of the step
     * @param title A string representing primary question of the Location question
     * @param answerFormat The formart in which the answer is expected
     */

    public LocationStep(String identifier, String title, String text, AnswerFormat answerFormat) {
        super(identifier,title,answerFormat);
        setText(text);


    }



}
