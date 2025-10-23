package landr.zk;

import landr.parser.CommandParseException;
import landr.parser.CommandString;
import landr.parser.ParserContext;
import landr.parser.syntax.Argument;
import landr.parser.syntax.CommandSyntax;

/** Command to print stats of a ZK node. */
class NodeStatDescriptor extends ZkCommandDescriptor<NodeStat> {

  private static final String NAME = "zk-stat";

  private static final CommandSyntax SYNTAX;

  static {
    SYNTAX = new CommandSyntax(NAME, PATH_PARAM, new Argument(PATH_PARAM, true));
  }

  NodeStatDescriptor() {
    super(SYNTAX);
  }

  @Override
  public NodeStat buildCommand(CommandString string, ParserContext context)
      throws CommandParseException {

    String path = getArgumentValue(PATH_PARAM, string, context);

    return new NodeStat(path);
  }
}
