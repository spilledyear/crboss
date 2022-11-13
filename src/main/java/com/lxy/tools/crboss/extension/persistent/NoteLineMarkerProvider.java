package com.lxy.tools.crboss.extension.persistent;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.execution.lineMarker.RunLineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class NoteLineMarkerProvider extends RunLineMarkerProvider {

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
//        return super.getLineMarkerInfo(element);

        return new RunLineMarkerInfo(element, AllIcons.Actions.Execute);
    }

    static class RunLineMarkerInfo extends LineMarkerInfo<PsiElement> {
        // 传入词法单元元素，以及对应的行标识符 Icon
        RunLineMarkerInfo(PsiElement element, Icon icon) {
            // 父类构造器，传入 Icon 、词法单元对象信息
            // 父类构造器的其他参数我们按需指定即可，run 字符串指的是鼠标放在 Icon 上时，
            // 给出的提示文本，GutterIconRenderer.Alignment.CENTER 值得是 Icon 在
            // 编辑器行显示的位置
            super(element, element.getTextRange(), icon, psiElement -> "Run", null, GutterIconRenderer.Alignment.CENTER, () -> "run");
        }
    }
}
