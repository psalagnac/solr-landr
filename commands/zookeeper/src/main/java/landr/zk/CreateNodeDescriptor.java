package landr.zk;

import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;
import org.apache.zookeeper.CreateMode;

class CreateNodeDescriptor extends ZkCommandDescriptor<CreateNode> {

  private static final String NAME = "zk-create";
  private static final String MODE_PARAM = "mode";

  private static final CommandSyntax SYNTAX;

  static {
    SYNTAX =
        new CommandSyntax(
            NAME,
            PATH_PARAM,
            new Argument(PATH_PARAM, true),
            new Argument(MODE_PARAM, CreateMode.PERSISTENT.name()));
  }

  CreateNodeDescriptor() {
    super(SYNTAX);
  }

  @Override
  public CreateNode buildCommand(CommandString string, ParserContext context)
      throws CommandParseException {

    String path = getArgumentValue(PATH_PARAM, string, context);
    CreateMode mode = getArgumentEnumValue(MODE_PARAM, string, context, CreateMode.class);
    return new CreateNode(path, mode);
  }
}
