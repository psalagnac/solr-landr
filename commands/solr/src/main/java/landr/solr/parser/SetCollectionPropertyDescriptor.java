package landr.solr.parser;

import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import landr.parser.syntax.ContextKey;
import landr.solr.cmd.SetCollectionProperty;

public class SetCollectionPropertyDescriptor extends AdminCommandDescriptor<SetCollectionProperty> {

  private static final String NAME = "set-collection-property";

  private static final String NAME_PARAM = "name";
  private static final String VALUE_PARAM = "value";

  private static final CommandSyntax SYNTAX;

  static {
    SYNTAX =
        new CommandSyntax(
            NAME,
            new Argument(COLLECTION_PARAM, true, ContextKey.COLLECTION_NAME),
            new Argument(NAME_PARAM, true),
            new Argument(VALUE_PARAM, true),
            new Argument(ASYNC_PARAM, "false"));
  }

  public SetCollectionPropertyDescriptor() {
    super(SYNTAX);
  }

  @Override
  public SetCollectionProperty buildCommand(CommandString string, ParserContext context)
      throws CommandParseException {

    SetCollectionProperty.Builder builder =
        parseCommonParams(string, context, SetCollectionProperty.Builder::new);

    String name = getArgumentValue(NAME_PARAM, string, context);
    builder.setName(name);

    String value = getArgumentValue(VALUE_PARAM, string, context);
    builder.setValue(value);

    return new SetCollectionProperty(builder);
  }
}
