package transferinterfaces;

import base.ruleset.RuleSet;

import java.util.Locale;

public interface RuleSetExporter {
    String getDescription(Locale usedLocale);
    void setup();
    RuleSet exportRuleSet();
}
