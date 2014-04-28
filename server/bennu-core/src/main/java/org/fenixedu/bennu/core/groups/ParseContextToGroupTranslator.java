package org.fenixedu.bennu.core.groups;

import java.util.List;

import org.fenixedu.bennu.core.groups.GroupParser.AndContext;
import org.fenixedu.bennu.core.groups.GroupParser.ArgumentContext;
import org.fenixedu.bennu.core.groups.GroupParser.AtomContext;
import org.fenixedu.bennu.core.groups.GroupParser.ExpressionContext;
import org.fenixedu.bennu.core.groups.GroupParser.FunctionContext;
import org.fenixedu.bennu.core.groups.GroupParser.LinkContext;
import org.fenixedu.bennu.core.groups.GroupParser.MinusContext;
import org.fenixedu.bennu.core.groups.GroupParser.NotContext;
import org.fenixedu.bennu.core.groups.GroupParser.OrContext;
import org.fenixedu.bennu.core.groups.GroupParser.ParseContext;
import org.fenixedu.bennu.core.groups.GroupParser.ValueContext;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

class ParseContextToGroupTranslator {
    public Group parse(ParseContext ctx) {
        return expression(ctx.expression());
    }

    private Group expression(ExpressionContext ctx) {
        return minus(ctx.minus());
    }

    private Group minus(MinusContext ctx) {
        List<OrContext> ors = ctx.or();
        if (ors.size() > 1) {
            Group group = null;
            for (OrContext or : ors) {
                if (group == null) {
                    group = or(or);
                } else {
                    group = group.minus(or(or));
                }
            }
            return group;
        } else {
            return or(ors.get(0));
        }
    }

    private Group or(OrContext ctx) {
        List<AndContext> ands = ctx.and();
        if (ands.size() > 1) {
            Group group = NobodyGroup.get();
            for (AndContext and : ands) {
                group = group.or(and(and));
            }
            return group;
        } else {
            return and(ands.get(0));
        }
    }

    private Group and(AndContext ctx) {
        List<NotContext> nots = ctx.not();
        if (nots.size() > 1) {
            Group group = AnyoneGroup.get();
            for (NotContext not : nots) {
                group = group.and(not(not));
            }
            return group;
        } else {
            return not(nots.get(0));
        }
    }

    private Group not(NotContext ctx) {
        Group atom = atom(ctx.atom());
        if (ctx.getStart().getText().equals("!")) {
            return atom.not();
        }
        return atom;
    }

    private Group atom(AtomContext ctx) {
        FunctionContext function = ctx.function();
        if (function != null) {
            return function(function);
        }
        LinkContext link = ctx.link();
        if (link != null) {
            return link(link);
        }
        ExpressionContext expr = ctx.expression();
        if (expr != null) {
            return expression(expr);
        }
        return null;
    }

    private Group link(LinkContext ctx) {
        return DynamicGroup.get(ctx.IDENTIFIER().getText());
    }

    private Group function(FunctionContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        Multimap<String, String> arguments = argument(ctx.argument());
        return CustomGroupRegistry.parse(name, arguments);
    }

    private Multimap<String, String> argument(List<ArgumentContext> ctx) {
        Multimap<String, String> arguments = ArrayListMultimap.create();
        for (ArgumentContext argument : ctx) {
            argument(arguments, argument);
        }
        return arguments;
    }

    private void argument(Multimap<String, String> arguments, ArgumentContext ctx) {
        String name = ctx.IDENTIFIER() != null ? ctx.IDENTIFIER().getText() : null;
        List<ValueContext> values = ctx.value();
        for (ValueContext value : values) {
            arguments.put(Strings.nullToEmpty(name), value(value));
        }
    }

    private String value(ValueContext ctx) {
        if (ctx.IDENTIFIER() != null) {
            return ctx.IDENTIFIER().getText();
        }
        String text = ctx.STRING().getText();
        return text.substring(1, text.length() - 1);
    }
}
