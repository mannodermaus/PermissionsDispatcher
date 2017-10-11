package permissions.dispatcher.detectors;

import com.android.tools.lint.checks.infrastructure.LintDetectorTest;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Issue;
import com.google.common.collect.ImmutableList;

import org.intellij.lang.annotations.Language;

import java.util.Collections;
import java.util.List;

public class PsiCallNeedsPermissionDetectorTest extends LintDetectorTest {

    private static final String NO_WARNINGS = "No warnings.";

    @Override
    protected Detector getDetector() {
        return new PsiCallNeedsPermissionDetector();
    }

    @Override
    protected List<Issue> getIssues() {
        return ImmutableList.of(PsiCallNeedsPermissionDetector.ISSUE);
    }

    public void testCallNeedsPermissionMethod() throws Exception {
        PsiCallNeedsPermissionDetector.methods = Collections.singletonList("fooBar");

        @Language("JAVA") String foo = ""
                + "package com.example;\n"
                + "public class Foo {\n"
                + "public void someMethod() {"
                + "Baz baz = new Baz();\n"
                + "baz.fooBar();  "
                + "}\n"
                + "}";

        @Language("JAVA") String baz = ""
                + "package com.example;\n"
                + "public class Baz {\n"
                + "public void fooBar() {\n"
                + "}\n"
                + "}";

        String result = lintProject(
                java("src/com/example/Foo.java", foo),
                java("src/com/example/Baz.java", baz));

        String error = ""
                + "src/com/example/Foo.java:4: Error: Trying to access permission-protected method directly "
                + "["
                + PsiCallNeedsPermissionDetector.ISSUE.getId()
                + "]\n"
                + "baz.fooBar();  }\n"
                + "~~~~~~~~~~~~\n"
                + "1 errors, 0 warnings\n";

        assertEquals(result, error);
    }

    public void testCallNeedsPermissionMethodNoError() throws Exception {

        @Language("JAVA") String foo = ""
                + "package com.example;\n"
                + "public class Foo {\n"
                + "public void someMethod() {"
                + "Baz baz = new Baz();\n"
                + "baz.noFooBar();\n"
                + "}\n"
                + "}";

        @Language("JAVA") String baz = ""
                + "package com.example;\n"
                + "public class Baz {\n"
                + "public void noFooBar() {\n"
                + "}\n"
                + "}";

        String result = lintProject(
                java("src/com/example/Foo.java", foo),
                java("src/com/example/Baz.java", baz));

        assertEquals(result, NO_WARNINGS);
    }
}
