package kr.ac.kaist.jsgen.js

import kr.ac.kaist.jsgen.js.ast._

trait ASTTransformer {
  def transform[T](opt: Option[T], t: T => T): Option[T] = opt.map(t)
  def transform(lex: Lexical): Lexical = lex

  def transform(ast: IdentifierReference): IdentifierReference = ast match {
    case IdentifierReference0(x0, params, span) =>
      IdentifierReference0(transform(x0), params, span)
    case IdentifierReference1(params, span) => ast
    case IdentifierReference2(params, span) => ast
  }
  def transform(ast: BindingIdentifier): BindingIdentifier = ast match {
    case BindingIdentifier0(x0, params, span) =>
      BindingIdentifier0(transform(x0), params, span)
    case BindingIdentifier1(params, span) => ast
    case BindingIdentifier2(params, span) => ast
  }
  def transform(ast: LabelIdentifier): LabelIdentifier = ast match {
    case LabelIdentifier0(x0, params, span) =>
      LabelIdentifier0(transform(x0), params, span)
    case LabelIdentifier1(params, span) => ast
    case LabelIdentifier2(params, span) => ast
  }
  def transform(ast: Identifier): Identifier = ast match {
    case Identifier0(x0, params, span) =>
      Identifier0(transform(x0), params, span)
  }
  def transform(ast: PrimaryExpression): PrimaryExpression = ast match {
    case PrimaryExpression0(params, span) => ast
    case PrimaryExpression1(x0, params, span) =>
      PrimaryExpression1(transform(x0), params, span)
    case PrimaryExpression2(x0, params, span) =>
      PrimaryExpression2(transform(x0), params, span)
    case PrimaryExpression3(x0, params, span) =>
      PrimaryExpression3(transform(x0), params, span)
    case PrimaryExpression4(x0, params, span) =>
      PrimaryExpression4(transform(x0), params, span)
    case PrimaryExpression5(x0, params, span) =>
      PrimaryExpression5(transform(x0), params, span)
    case PrimaryExpression6(x0, params, span) =>
      PrimaryExpression6(transform(x0), params, span)
    case PrimaryExpression7(x0, params, span) =>
      PrimaryExpression7(transform(x0), params, span)
    case PrimaryExpression8(x0, params, span) =>
      PrimaryExpression8(transform(x0), params, span)
    case PrimaryExpression9(x0, params, span) =>
      PrimaryExpression9(transform(x0), params, span)
    case PrimaryExpression10(x0, params, span) =>
      PrimaryExpression10(transform(x0), params, span)
    case PrimaryExpression11(x0, params, span) =>
      PrimaryExpression11(transform(x0), params, span)
    case PrimaryExpression12(x0, params, span) =>
      PrimaryExpression12(transform(x0), params, span)
  }
  def transform(ast: CoverParenthesizedExpressionAndArrowParameterList): CoverParenthesizedExpressionAndArrowParameterList = ast match {
    case CoverParenthesizedExpressionAndArrowParameterList0(x1, params, span) =>
      CoverParenthesizedExpressionAndArrowParameterList0(transform(x1), params, span)
    case CoverParenthesizedExpressionAndArrowParameterList1(x1, params, span) =>
      CoverParenthesizedExpressionAndArrowParameterList1(transform(x1), params, span)
    case CoverParenthesizedExpressionAndArrowParameterList2(params, span) => ast
    case CoverParenthesizedExpressionAndArrowParameterList3(x2, params, span) =>
      CoverParenthesizedExpressionAndArrowParameterList3(transform(x2), params, span)
    case CoverParenthesizedExpressionAndArrowParameterList4(x2, params, span) =>
      CoverParenthesizedExpressionAndArrowParameterList4(transform(x2), params, span)
    case CoverParenthesizedExpressionAndArrowParameterList5(x1, x4, params, span) =>
      CoverParenthesizedExpressionAndArrowParameterList5(transform(x1), transform(x4), params, span)
    case CoverParenthesizedExpressionAndArrowParameterList6(x1, x4, params, span) =>
      CoverParenthesizedExpressionAndArrowParameterList6(transform(x1), transform(x4), params, span)
  }
  def transform(ast: ParenthesizedExpression): ParenthesizedExpression = ast match {
    case ParenthesizedExpression0(x1, params, span) =>
      ParenthesizedExpression0(transform(x1), params, span)
  }
  def transform(ast: Literal): Literal = ast match {
    case Literal0(x0, params, span) =>
      Literal0(transform(x0), params, span)
    case Literal1(x0, params, span) =>
      Literal1(transform(x0), params, span)
    case Literal2(x0, params, span) =>
      Literal2(transform(x0), params, span)
    case Literal3(x0, params, span) =>
      Literal3(transform(x0), params, span)
  }
  def transform(ast: ArrayLiteral): ArrayLiteral = ast match {
    case ArrayLiteral0(x1, params, span) =>
      ArrayLiteral0(transform[Elision](x1, transform), params, span)
    case ArrayLiteral1(x1, params, span) =>
      ArrayLiteral1(transform(x1), params, span)
    case ArrayLiteral2(x1, x3, params, span) =>
      ArrayLiteral2(transform(x1), transform[Elision](x3, transform), params, span)
  }
  def transform(ast: ElementList): ElementList = ast match {
    case ElementList0(x0, x1, params, span) =>
      ElementList0(transform[Elision](x0, transform), transform(x1), params, span)
    case ElementList1(x0, x1, params, span) =>
      ElementList1(transform[Elision](x0, transform), transform(x1), params, span)
    case ElementList2(x0, x2, x3, params, span) =>
      ElementList2(transform(x0), transform[Elision](x2, transform), transform(x3), params, span)
    case ElementList3(x0, x2, x3, params, span) =>
      ElementList3(transform(x0), transform[Elision](x2, transform), transform(x3), params, span)
  }
  def transform(ast: Elision): Elision = ast match {
    case Elision0(params, span) => ast
    case Elision1(x0, params, span) =>
      Elision1(transform(x0), params, span)
  }
  def transform(ast: SpreadElement): SpreadElement = ast match {
    case SpreadElement0(x1, params, span) =>
      SpreadElement0(transform(x1), params, span)
  }
  def transform(ast: ObjectLiteral): ObjectLiteral = ast match {
    case ObjectLiteral0(params, span) => ast
    case ObjectLiteral1(x1, params, span) =>
      ObjectLiteral1(transform(x1), params, span)
    case ObjectLiteral2(x1, params, span) =>
      ObjectLiteral2(transform(x1), params, span)
  }
  def transform(ast: PropertyDefinitionList): PropertyDefinitionList = ast match {
    case PropertyDefinitionList0(x0, params, span) =>
      PropertyDefinitionList0(transform(x0), params, span)
    case PropertyDefinitionList1(x0, x2, params, span) =>
      PropertyDefinitionList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: PropertyDefinition): PropertyDefinition = ast match {
    case PropertyDefinition0(x0, params, span) =>
      PropertyDefinition0(transform(x0), params, span)
    case PropertyDefinition1(x0, params, span) =>
      PropertyDefinition1(transform(x0), params, span)
    case PropertyDefinition2(x0, x2, params, span) =>
      PropertyDefinition2(transform(x0), transform(x2), params, span)
    case PropertyDefinition3(x0, params, span) =>
      PropertyDefinition3(transform(x0), params, span)
    case PropertyDefinition4(x1, params, span) =>
      PropertyDefinition4(transform(x1), params, span)
  }
  def transform(ast: PropertyName): PropertyName = ast match {
    case PropertyName0(x0, params, span) =>
      PropertyName0(transform(x0), params, span)
    case PropertyName1(x0, params, span) =>
      PropertyName1(transform(x0), params, span)
  }
  def transform(ast: LiteralPropertyName): LiteralPropertyName = ast match {
    case LiteralPropertyName0(x0, params, span) =>
      LiteralPropertyName0(transform(x0), params, span)
    case LiteralPropertyName1(x0, params, span) =>
      LiteralPropertyName1(transform(x0), params, span)
    case LiteralPropertyName2(x0, params, span) =>
      LiteralPropertyName2(transform(x0), params, span)
  }
  def transform(ast: ComputedPropertyName): ComputedPropertyName = ast match {
    case ComputedPropertyName0(x1, params, span) =>
      ComputedPropertyName0(transform(x1), params, span)
  }
  def transform(ast: CoverInitializedName): CoverInitializedName = ast match {
    case CoverInitializedName0(x0, x1, params, span) =>
      CoverInitializedName0(transform(x0), transform(x1), params, span)
  }
  def transform(ast: Initializer): Initializer = ast match {
    case Initializer0(x1, params, span) =>
      Initializer0(transform(x1), params, span)
  }
  def transform(ast: TemplateLiteral): TemplateLiteral = ast match {
    case TemplateLiteral0(x0, params, span) =>
      TemplateLiteral0(transform(x0), params, span)
    case TemplateLiteral1(x0, params, span) =>
      TemplateLiteral1(transform(x0), params, span)
  }
  def transform(ast: SubstitutionTemplate): SubstitutionTemplate = ast match {
    case SubstitutionTemplate0(x0, x1, x2, params, span) =>
      SubstitutionTemplate0(transform(x0), transform(x1), transform(x2), params, span)
  }
  def transform(ast: TemplateSpans): TemplateSpans = ast match {
    case TemplateSpans0(x0, params, span) =>
      TemplateSpans0(transform(x0), params, span)
    case TemplateSpans1(x0, x1, params, span) =>
      TemplateSpans1(transform(x0), transform(x1), params, span)
  }
  def transform(ast: TemplateMiddleList): TemplateMiddleList = ast match {
    case TemplateMiddleList0(x0, x1, params, span) =>
      TemplateMiddleList0(transform(x0), transform(x1), params, span)
    case TemplateMiddleList1(x0, x1, x2, params, span) =>
      TemplateMiddleList1(transform(x0), transform(x1), transform(x2), params, span)
  }
  def transform(ast: MemberExpression): MemberExpression = ast match {
    case MemberExpression0(x0, params, span) =>
      MemberExpression0(transform(x0), params, span)
    case MemberExpression1(x0, x2, params, span) =>
      MemberExpression1(transform(x0), transform(x2), params, span)
    case MemberExpression2(x0, x2, params, span) =>
      MemberExpression2(transform(x0), transform(x2), params, span)
    case MemberExpression3(x0, x1, params, span) =>
      MemberExpression3(transform(x0), transform(x1), params, span)
    case MemberExpression4(x0, params, span) =>
      MemberExpression4(transform(x0), params, span)
    case MemberExpression5(x0, params, span) =>
      MemberExpression5(transform(x0), params, span)
    case MemberExpression6(x1, x2, params, span) =>
      MemberExpression6(transform(x1), transform(x2), params, span)
  }
  def transform(ast: SuperProperty): SuperProperty = ast match {
    case SuperProperty0(x2, params, span) =>
      SuperProperty0(transform(x2), params, span)
    case SuperProperty1(x2, params, span) =>
      SuperProperty1(transform(x2), params, span)
  }
  def transform(ast: MetaProperty): MetaProperty = ast match {
    case MetaProperty0(x0, params, span) =>
      MetaProperty0(transform(x0), params, span)
    case MetaProperty1(x0, params, span) =>
      MetaProperty1(transform(x0), params, span)
  }
  def transform(ast: NewTarget): NewTarget = ast match {
    case NewTarget0(params, span) => ast
  }
  def transform(ast: ImportMeta): ImportMeta = ast match {
    case ImportMeta0(params, span) => ast
  }
  def transform(ast: NewExpression): NewExpression = ast match {
    case NewExpression0(x0, params, span) =>
      NewExpression0(transform(x0), params, span)
    case NewExpression1(x1, params, span) =>
      NewExpression1(transform(x1), params, span)
  }
  def transform(ast: CallExpression): CallExpression = ast match {
    case CallExpression0(x0, params, span) =>
      CallExpression0(transform(x0), params, span)
    case CallExpression1(x0, params, span) =>
      CallExpression1(transform(x0), params, span)
    case CallExpression2(x0, params, span) =>
      CallExpression2(transform(x0), params, span)
    case CallExpression3(x0, x1, params, span) =>
      CallExpression3(transform(x0), transform(x1), params, span)
    case CallExpression4(x0, x2, params, span) =>
      CallExpression4(transform(x0), transform(x2), params, span)
    case CallExpression5(x0, x2, params, span) =>
      CallExpression5(transform(x0), transform(x2), params, span)
    case CallExpression6(x0, x1, params, span) =>
      CallExpression6(transform(x0), transform(x1), params, span)
  }
  def transform(ast: SuperCall): SuperCall = ast match {
    case SuperCall0(x1, params, span) =>
      SuperCall0(transform(x1), params, span)
  }
  def transform(ast: ImportCall): ImportCall = ast match {
    case ImportCall0(x2, params, span) =>
      ImportCall0(transform(x2), params, span)
  }
  def transform(ast: Arguments): Arguments = ast match {
    case Arguments0(params, span) => ast
    case Arguments1(x1, params, span) =>
      Arguments1(transform(x1), params, span)
    case Arguments2(x1, params, span) =>
      Arguments2(transform(x1), params, span)
  }
  def transform(ast: ArgumentList): ArgumentList = ast match {
    case ArgumentList0(x0, params, span) =>
      ArgumentList0(transform(x0), params, span)
    case ArgumentList1(x1, params, span) =>
      ArgumentList1(transform(x1), params, span)
    case ArgumentList2(x0, x2, params, span) =>
      ArgumentList2(transform(x0), transform(x2), params, span)
    case ArgumentList3(x0, x3, params, span) =>
      ArgumentList3(transform(x0), transform(x3), params, span)
  }
  def transform(ast: OptionalExpression): OptionalExpression = ast match {
    case OptionalExpression0(x0, x1, params, span) =>
      OptionalExpression0(transform(x0), transform(x1), params, span)
    case OptionalExpression1(x0, x1, params, span) =>
      OptionalExpression1(transform(x0), transform(x1), params, span)
    case OptionalExpression2(x0, x1, params, span) =>
      OptionalExpression2(transform(x0), transform(x1), params, span)
  }
  def transform(ast: OptionalChain): OptionalChain = ast match {
    case OptionalChain0(x1, params, span) =>
      OptionalChain0(transform(x1), params, span)
    case OptionalChain1(x2, params, span) =>
      OptionalChain1(transform(x2), params, span)
    case OptionalChain2(x1, params, span) =>
      OptionalChain2(transform(x1), params, span)
    case OptionalChain3(x1, params, span) =>
      OptionalChain3(transform(x1), params, span)
    case OptionalChain4(x0, x1, params, span) =>
      OptionalChain4(transform(x0), transform(x1), params, span)
    case OptionalChain5(x0, x2, params, span) =>
      OptionalChain5(transform(x0), transform(x2), params, span)
    case OptionalChain6(x0, x2, params, span) =>
      OptionalChain6(transform(x0), transform(x2), params, span)
    case OptionalChain7(x0, x1, params, span) =>
      OptionalChain7(transform(x0), transform(x1), params, span)
  }
  def transform(ast: LeftHandSideExpression): LeftHandSideExpression = ast match {
    case LeftHandSideExpression0(x0, params, span) =>
      LeftHandSideExpression0(transform(x0), params, span)
    case LeftHandSideExpression1(x0, params, span) =>
      LeftHandSideExpression1(transform(x0), params, span)
    case LeftHandSideExpression2(x0, params, span) =>
      LeftHandSideExpression2(transform(x0), params, span)
  }
  def transform(ast: CallMemberExpression): CallMemberExpression = ast match {
    case CallMemberExpression0(x0, x1, params, span) =>
      CallMemberExpression0(transform(x0), transform(x1), params, span)
  }
  def transform(ast: UpdateExpression): UpdateExpression = ast match {
    case UpdateExpression0(x0, params, span) =>
      UpdateExpression0(transform(x0), params, span)
    case UpdateExpression1(x0, params, span) =>
      UpdateExpression1(transform(x0), params, span)
    case UpdateExpression2(x0, params, span) =>
      UpdateExpression2(transform(x0), params, span)
    case UpdateExpression3(x1, params, span) =>
      UpdateExpression3(transform(x1), params, span)
    case UpdateExpression4(x1, params, span) =>
      UpdateExpression4(transform(x1), params, span)
  }
  def transform(ast: UnaryExpression): UnaryExpression = ast match {
    case UnaryExpression0(x0, params, span) =>
      UnaryExpression0(transform(x0), params, span)
    case UnaryExpression1(x1, params, span) =>
      UnaryExpression1(transform(x1), params, span)
    case UnaryExpression2(x1, params, span) =>
      UnaryExpression2(transform(x1), params, span)
    case UnaryExpression3(x1, params, span) =>
      UnaryExpression3(transform(x1), params, span)
    case UnaryExpression4(x1, params, span) =>
      UnaryExpression4(transform(x1), params, span)
    case UnaryExpression5(x1, params, span) =>
      UnaryExpression5(transform(x1), params, span)
    case UnaryExpression6(x1, params, span) =>
      UnaryExpression6(transform(x1), params, span)
    case UnaryExpression7(x1, params, span) =>
      UnaryExpression7(transform(x1), params, span)
    case UnaryExpression8(x0, params, span) =>
      UnaryExpression8(transform(x0), params, span)
  }
  def transform(ast: ExponentiationExpression): ExponentiationExpression = ast match {
    case ExponentiationExpression0(x0, params, span) =>
      ExponentiationExpression0(transform(x0), params, span)
    case ExponentiationExpression1(x0, x2, params, span) =>
      ExponentiationExpression1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: MultiplicativeExpression): MultiplicativeExpression = ast match {
    case MultiplicativeExpression0(x0, params, span) =>
      MultiplicativeExpression0(transform(x0), params, span)
    case MultiplicativeExpression1(x0, x1, x2, params, span) =>
      MultiplicativeExpression1(transform(x0), transform(x1), transform(x2), params, span)
  }
  def transform(ast: MultiplicativeOperator): MultiplicativeOperator = ast match {
    case MultiplicativeOperator0(params, span) => ast
    case MultiplicativeOperator1(params, span) => ast
    case MultiplicativeOperator2(params, span) => ast
  }
  def transform(ast: AdditiveExpression): AdditiveExpression = ast match {
    case AdditiveExpression0(x0, params, span) =>
      AdditiveExpression0(transform(x0), params, span)
    case AdditiveExpression1(x0, x2, params, span) =>
      AdditiveExpression1(transform(x0), transform(x2), params, span)
    case AdditiveExpression2(x0, x2, params, span) =>
      AdditiveExpression2(transform(x0), transform(x2), params, span)
  }
  def transform(ast: ShiftExpression): ShiftExpression = ast match {
    case ShiftExpression0(x0, params, span) =>
      ShiftExpression0(transform(x0), params, span)
    case ShiftExpression1(x0, x2, params, span) =>
      ShiftExpression1(transform(x0), transform(x2), params, span)
    case ShiftExpression2(x0, x2, params, span) =>
      ShiftExpression2(transform(x0), transform(x2), params, span)
    case ShiftExpression3(x0, x2, params, span) =>
      ShiftExpression3(transform(x0), transform(x2), params, span)
  }
  def transform(ast: RelationalExpression): RelationalExpression = ast match {
    case RelationalExpression0(x0, params, span) =>
      RelationalExpression0(transform(x0), params, span)
    case RelationalExpression1(x0, x2, params, span) =>
      RelationalExpression1(transform(x0), transform(x2), params, span)
    case RelationalExpression2(x0, x2, params, span) =>
      RelationalExpression2(transform(x0), transform(x2), params, span)
    case RelationalExpression3(x0, x2, params, span) =>
      RelationalExpression3(transform(x0), transform(x2), params, span)
    case RelationalExpression4(x0, x2, params, span) =>
      RelationalExpression4(transform(x0), transform(x2), params, span)
    case RelationalExpression5(x0, x2, params, span) =>
      RelationalExpression5(transform(x0), transform(x2), params, span)
    case RelationalExpression6(x0, x2, params, span) =>
      RelationalExpression6(transform(x0), transform(x2), params, span)
  }
  def transform(ast: EqualityExpression): EqualityExpression = ast match {
    case EqualityExpression0(x0, params, span) =>
      EqualityExpression0(transform(x0), params, span)
    case EqualityExpression1(x0, x2, params, span) =>
      EqualityExpression1(transform(x0), transform(x2), params, span)
    case EqualityExpression2(x0, x2, params, span) =>
      EqualityExpression2(transform(x0), transform(x2), params, span)
    case EqualityExpression3(x0, x2, params, span) =>
      EqualityExpression3(transform(x0), transform(x2), params, span)
    case EqualityExpression4(x0, x2, params, span) =>
      EqualityExpression4(transform(x0), transform(x2), params, span)
  }
  def transform(ast: BitwiseANDExpression): BitwiseANDExpression = ast match {
    case BitwiseANDExpression0(x0, params, span) =>
      BitwiseANDExpression0(transform(x0), params, span)
    case BitwiseANDExpression1(x0, x2, params, span) =>
      BitwiseANDExpression1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: BitwiseXORExpression): BitwiseXORExpression = ast match {
    case BitwiseXORExpression0(x0, params, span) =>
      BitwiseXORExpression0(transform(x0), params, span)
    case BitwiseXORExpression1(x0, x2, params, span) =>
      BitwiseXORExpression1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: BitwiseORExpression): BitwiseORExpression = ast match {
    case BitwiseORExpression0(x0, params, span) =>
      BitwiseORExpression0(transform(x0), params, span)
    case BitwiseORExpression1(x0, x2, params, span) =>
      BitwiseORExpression1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: LogicalANDExpression): LogicalANDExpression = ast match {
    case LogicalANDExpression0(x0, params, span) =>
      LogicalANDExpression0(transform(x0), params, span)
    case LogicalANDExpression1(x0, x2, params, span) =>
      LogicalANDExpression1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: LogicalORExpression): LogicalORExpression = ast match {
    case LogicalORExpression0(x0, params, span) =>
      LogicalORExpression0(transform(x0), params, span)
    case LogicalORExpression1(x0, x2, params, span) =>
      LogicalORExpression1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: CoalesceExpression): CoalesceExpression = ast match {
    case CoalesceExpression0(x0, x2, params, span) =>
      CoalesceExpression0(transform(x0), transform(x2), params, span)
  }
  def transform(ast: CoalesceExpressionHead): CoalesceExpressionHead = ast match {
    case CoalesceExpressionHead0(x0, params, span) =>
      CoalesceExpressionHead0(transform(x0), params, span)
    case CoalesceExpressionHead1(x0, params, span) =>
      CoalesceExpressionHead1(transform(x0), params, span)
  }
  def transform(ast: ShortCircuitExpression): ShortCircuitExpression = ast match {
    case ShortCircuitExpression0(x0, params, span) =>
      ShortCircuitExpression0(transform(x0), params, span)
    case ShortCircuitExpression1(x0, params, span) =>
      ShortCircuitExpression1(transform(x0), params, span)
  }
  def transform(ast: ConditionalExpression): ConditionalExpression = ast match {
    case ConditionalExpression0(x0, params, span) =>
      ConditionalExpression0(transform(x0), params, span)
    case ConditionalExpression1(x0, x2, x4, params, span) =>
      ConditionalExpression1(transform(x0), transform(x2), transform(x4), params, span)
  }
  def transform(ast: AssignmentExpression): AssignmentExpression = ast match {
    case AssignmentExpression0(x0, params, span) =>
      AssignmentExpression0(transform(x0), params, span)
    case AssignmentExpression1(x0, params, span) =>
      AssignmentExpression1(transform(x0), params, span)
    case AssignmentExpression2(x0, params, span) =>
      AssignmentExpression2(transform(x0), params, span)
    case AssignmentExpression3(x0, params, span) =>
      AssignmentExpression3(transform(x0), params, span)
    case AssignmentExpression4(x0, x2, params, span) =>
      AssignmentExpression4(transform(x0), transform(x2), params, span)
    case AssignmentExpression5(x0, x1, x2, params, span) =>
      AssignmentExpression5(transform(x0), transform(x1), transform(x2), params, span)
    case AssignmentExpression6(x0, x2, params, span) =>
      AssignmentExpression6(transform(x0), transform(x2), params, span)
    case AssignmentExpression7(x0, x2, params, span) =>
      AssignmentExpression7(transform(x0), transform(x2), params, span)
    case AssignmentExpression8(x0, x2, params, span) =>
      AssignmentExpression8(transform(x0), transform(x2), params, span)
  }
  def transform(ast: AssignmentOperator): AssignmentOperator = ast match {
    case AssignmentOperator0(params, span) => ast
    case AssignmentOperator1(params, span) => ast
    case AssignmentOperator2(params, span) => ast
    case AssignmentOperator3(params, span) => ast
    case AssignmentOperator4(params, span) => ast
    case AssignmentOperator5(params, span) => ast
    case AssignmentOperator6(params, span) => ast
    case AssignmentOperator7(params, span) => ast
    case AssignmentOperator8(params, span) => ast
    case AssignmentOperator9(params, span) => ast
    case AssignmentOperator10(params, span) => ast
    case AssignmentOperator11(params, span) => ast
  }
  def transform(ast: AssignmentPattern): AssignmentPattern = ast match {
    case AssignmentPattern0(x0, params, span) =>
      AssignmentPattern0(transform(x0), params, span)
    case AssignmentPattern1(x0, params, span) =>
      AssignmentPattern1(transform(x0), params, span)
  }
  def transform(ast: ObjectAssignmentPattern): ObjectAssignmentPattern = ast match {
    case ObjectAssignmentPattern0(params, span) => ast
    case ObjectAssignmentPattern1(x1, params, span) =>
      ObjectAssignmentPattern1(transform(x1), params, span)
    case ObjectAssignmentPattern2(x1, params, span) =>
      ObjectAssignmentPattern2(transform(x1), params, span)
    case ObjectAssignmentPattern3(x1, x3, params, span) =>
      ObjectAssignmentPattern3(transform(x1), transform[AssignmentRestProperty](x3, transform), params, span)
  }
  def transform(ast: ArrayAssignmentPattern): ArrayAssignmentPattern = ast match {
    case ArrayAssignmentPattern0(x1, x2, params, span) =>
      ArrayAssignmentPattern0(transform[Elision](x1, transform), transform[AssignmentRestElement](x2, transform), params, span)
    case ArrayAssignmentPattern1(x1, params, span) =>
      ArrayAssignmentPattern1(transform(x1), params, span)
    case ArrayAssignmentPattern2(x1, x3, x4, params, span) =>
      ArrayAssignmentPattern2(transform(x1), transform[Elision](x3, transform), transform[AssignmentRestElement](x4, transform), params, span)
  }
  def transform(ast: AssignmentRestProperty): AssignmentRestProperty = ast match {
    case AssignmentRestProperty0(x1, params, span) =>
      AssignmentRestProperty0(transform(x1), params, span)
  }
  def transform(ast: AssignmentPropertyList): AssignmentPropertyList = ast match {
    case AssignmentPropertyList0(x0, params, span) =>
      AssignmentPropertyList0(transform(x0), params, span)
    case AssignmentPropertyList1(x0, x2, params, span) =>
      AssignmentPropertyList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: AssignmentElementList): AssignmentElementList = ast match {
    case AssignmentElementList0(x0, params, span) =>
      AssignmentElementList0(transform(x0), params, span)
    case AssignmentElementList1(x0, x2, params, span) =>
      AssignmentElementList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: AssignmentElisionElement): AssignmentElisionElement = ast match {
    case AssignmentElisionElement0(x0, x1, params, span) =>
      AssignmentElisionElement0(transform[Elision](x0, transform), transform(x1), params, span)
  }
  def transform(ast: AssignmentProperty): AssignmentProperty = ast match {
    case AssignmentProperty0(x0, x1, params, span) =>
      AssignmentProperty0(transform(x0), transform[Initializer](x1, transform), params, span)
    case AssignmentProperty1(x0, x2, params, span) =>
      AssignmentProperty1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: AssignmentElement): AssignmentElement = ast match {
    case AssignmentElement0(x0, x1, params, span) =>
      AssignmentElement0(transform(x0), transform[Initializer](x1, transform), params, span)
  }
  def transform(ast: AssignmentRestElement): AssignmentRestElement = ast match {
    case AssignmentRestElement0(x1, params, span) =>
      AssignmentRestElement0(transform(x1), params, span)
  }
  def transform(ast: DestructuringAssignmentTarget): DestructuringAssignmentTarget = ast match {
    case DestructuringAssignmentTarget0(x0, params, span) =>
      DestructuringAssignmentTarget0(transform(x0), params, span)
  }
  def transform(ast: Expression): Expression = ast match {
    case Expression0(x0, params, span) =>
      Expression0(transform(x0), params, span)
    case Expression1(x0, x2, params, span) =>
      Expression1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: Statement): Statement = ast match {
    case Statement0(x0, params, span) =>
      Statement0(transform(x0), params, span)
    case Statement1(x0, params, span) =>
      Statement1(transform(x0), params, span)
    case Statement2(x0, params, span) =>
      Statement2(transform(x0), params, span)
    case Statement3(x0, params, span) =>
      Statement3(transform(x0), params, span)
    case Statement4(x0, params, span) =>
      Statement4(transform(x0), params, span)
    case Statement5(x0, params, span) =>
      Statement5(transform(x0), params, span)
    case Statement6(x0, params, span) =>
      Statement6(transform(x0), params, span)
    case Statement7(x0, params, span) =>
      Statement7(transform(x0), params, span)
    case Statement8(x0, params, span) =>
      Statement8(transform(x0), params, span)
    case Statement9(x0, params, span) =>
      Statement9(transform(x0), params, span)
    case Statement10(x0, params, span) =>
      Statement10(transform(x0), params, span)
    case Statement11(x0, params, span) =>
      Statement11(transform(x0), params, span)
    case Statement12(x0, params, span) =>
      Statement12(transform(x0), params, span)
    case Statement13(x0, params, span) =>
      Statement13(transform(x0), params, span)
  }
  def transform(ast: Declaration): Declaration = ast match {
    case Declaration0(x0, params, span) =>
      Declaration0(transform(x0), params, span)
    case Declaration1(x0, params, span) =>
      Declaration1(transform(x0), params, span)
    case Declaration2(x0, params, span) =>
      Declaration2(transform(x0), params, span)
  }
  def transform(ast: HoistableDeclaration): HoistableDeclaration = ast match {
    case HoistableDeclaration0(x0, params, span) =>
      HoistableDeclaration0(transform(x0), params, span)
    case HoistableDeclaration1(x0, params, span) =>
      HoistableDeclaration1(transform(x0), params, span)
    case HoistableDeclaration2(x0, params, span) =>
      HoistableDeclaration2(transform(x0), params, span)
    case HoistableDeclaration3(x0, params, span) =>
      HoistableDeclaration3(transform(x0), params, span)
  }
  def transform(ast: BreakableStatement): BreakableStatement = ast match {
    case BreakableStatement0(x0, params, span) =>
      BreakableStatement0(transform(x0), params, span)
    case BreakableStatement1(x0, params, span) =>
      BreakableStatement1(transform(x0), params, span)
  }
  def transform(ast: BlockStatement): BlockStatement = ast match {
    case BlockStatement0(x0, params, span) =>
      BlockStatement0(transform(x0), params, span)
  }
  def transform(ast: Block): Block = ast match {
    case Block0(x1, params, span) =>
      Block0(transform[StatementList](x1, transform), params, span)
  }
  def transform(ast: StatementList): StatementList = ast match {
    case StatementList0(x0, params, span) =>
      StatementList0(transform(x0), params, span)
    case StatementList1(x0, x1, params, span) =>
      StatementList1(transform(x0), transform(x1), params, span)
  }
  def transform(ast: StatementListItem): StatementListItem = ast match {
    case StatementListItem0(x0, params, span) =>
      StatementListItem0(transform(x0), params, span)
    case StatementListItem1(x0, params, span) =>
      StatementListItem1(transform(x0), params, span)
  }
  def transform(ast: LexicalDeclaration): LexicalDeclaration = ast match {
    case LexicalDeclaration0(x0, x1, params, span) =>
      LexicalDeclaration0(transform(x0), transform(x1), params, span)
  }
  def transform(ast: LetOrConst): LetOrConst = ast match {
    case LetOrConst0(params, span) => ast
    case LetOrConst1(params, span) => ast
  }
  def transform(ast: BindingList): BindingList = ast match {
    case BindingList0(x0, params, span) =>
      BindingList0(transform(x0), params, span)
    case BindingList1(x0, x2, params, span) =>
      BindingList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: LexicalBinding): LexicalBinding = ast match {
    case LexicalBinding0(x0, x1, params, span) =>
      LexicalBinding0(transform(x0), transform[Initializer](x1, transform), params, span)
    case LexicalBinding1(x0, x1, params, span) =>
      LexicalBinding1(transform(x0), transform(x1), params, span)
  }
  def transform(ast: VariableStatement): VariableStatement = ast match {
    case VariableStatement0(x1, params, span) =>
      VariableStatement0(transform(x1), params, span)
  }
  def transform(ast: VariableDeclarationList): VariableDeclarationList = ast match {
    case VariableDeclarationList0(x0, params, span) =>
      VariableDeclarationList0(transform(x0), params, span)
    case VariableDeclarationList1(x0, x2, params, span) =>
      VariableDeclarationList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: VariableDeclaration): VariableDeclaration = ast match {
    case VariableDeclaration0(x0, x1, params, span) =>
      VariableDeclaration0(transform(x0), transform[Initializer](x1, transform), params, span)
    case VariableDeclaration1(x0, x1, params, span) =>
      VariableDeclaration1(transform(x0), transform(x1), params, span)
  }
  def transform(ast: BindingPattern): BindingPattern = ast match {
    case BindingPattern0(x0, params, span) =>
      BindingPattern0(transform(x0), params, span)
    case BindingPattern1(x0, params, span) =>
      BindingPattern1(transform(x0), params, span)
  }
  def transform(ast: ObjectBindingPattern): ObjectBindingPattern = ast match {
    case ObjectBindingPattern0(params, span) => ast
    case ObjectBindingPattern1(x1, params, span) =>
      ObjectBindingPattern1(transform(x1), params, span)
    case ObjectBindingPattern2(x1, params, span) =>
      ObjectBindingPattern2(transform(x1), params, span)
    case ObjectBindingPattern3(x1, x3, params, span) =>
      ObjectBindingPattern3(transform(x1), transform[BindingRestProperty](x3, transform), params, span)
  }
  def transform(ast: ArrayBindingPattern): ArrayBindingPattern = ast match {
    case ArrayBindingPattern0(x1, x2, params, span) =>
      ArrayBindingPattern0(transform[Elision](x1, transform), transform[BindingRestElement](x2, transform), params, span)
    case ArrayBindingPattern1(x1, params, span) =>
      ArrayBindingPattern1(transform(x1), params, span)
    case ArrayBindingPattern2(x1, x3, x4, params, span) =>
      ArrayBindingPattern2(transform(x1), transform[Elision](x3, transform), transform[BindingRestElement](x4, transform), params, span)
  }
  def transform(ast: BindingRestProperty): BindingRestProperty = ast match {
    case BindingRestProperty0(x1, params, span) =>
      BindingRestProperty0(transform(x1), params, span)
  }
  def transform(ast: BindingPropertyList): BindingPropertyList = ast match {
    case BindingPropertyList0(x0, params, span) =>
      BindingPropertyList0(transform(x0), params, span)
    case BindingPropertyList1(x0, x2, params, span) =>
      BindingPropertyList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: BindingElementList): BindingElementList = ast match {
    case BindingElementList0(x0, params, span) =>
      BindingElementList0(transform(x0), params, span)
    case BindingElementList1(x0, x2, params, span) =>
      BindingElementList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: BindingElisionElement): BindingElisionElement = ast match {
    case BindingElisionElement0(x0, x1, params, span) =>
      BindingElisionElement0(transform[Elision](x0, transform), transform(x1), params, span)
  }
  def transform(ast: BindingProperty): BindingProperty = ast match {
    case BindingProperty0(x0, params, span) =>
      BindingProperty0(transform(x0), params, span)
    case BindingProperty1(x0, x2, params, span) =>
      BindingProperty1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: BindingElement): BindingElement = ast match {
    case BindingElement0(x0, params, span) =>
      BindingElement0(transform(x0), params, span)
    case BindingElement1(x0, x1, params, span) =>
      BindingElement1(transform(x0), transform[Initializer](x1, transform), params, span)
  }
  def transform(ast: SingleNameBinding): SingleNameBinding = ast match {
    case SingleNameBinding0(x0, x1, params, span) =>
      SingleNameBinding0(transform(x0), transform[Initializer](x1, transform), params, span)
  }
  def transform(ast: BindingRestElement): BindingRestElement = ast match {
    case BindingRestElement0(x1, params, span) =>
      BindingRestElement0(transform(x1), params, span)
    case BindingRestElement1(x1, params, span) =>
      BindingRestElement1(transform(x1), params, span)
  }
  def transform(ast: EmptyStatement): EmptyStatement = ast match {
    case EmptyStatement0(params, span) => ast
  }
  def transform(ast: ExpressionStatement): ExpressionStatement = ast match {
    case ExpressionStatement0(x1, params, span) =>
      ExpressionStatement0(transform(x1), params, span)
  }
  def transform(ast: IfStatement): IfStatement = ast match {
    case IfStatement0(x2, x4, x6, params, span) =>
      IfStatement0(transform(x2), transform(x4), transform(x6), params, span)
    case IfStatement1(x2, x4, params, span) =>
      IfStatement1(transform(x2), transform(x4), params, span)
  }
  def transform(ast: IterationStatement): IterationStatement = ast match {
    case IterationStatement0(x0, params, span) =>
      IterationStatement0(transform(x0), params, span)
    case IterationStatement1(x0, params, span) =>
      IterationStatement1(transform(x0), params, span)
    case IterationStatement2(x0, params, span) =>
      IterationStatement2(transform(x0), params, span)
    case IterationStatement3(x0, params, span) =>
      IterationStatement3(transform(x0), params, span)
  }
  def transform(ast: DoWhileStatement): DoWhileStatement = ast match {
    case DoWhileStatement0(x1, x4, params, span) =>
      DoWhileStatement0(transform(x1), transform(x4), params, span)
  }
  def transform(ast: WhileStatement): WhileStatement = ast match {
    case WhileStatement0(x2, x4, params, span) =>
      WhileStatement0(transform(x2), transform(x4), params, span)
  }
  def transform(ast: ForStatement): ForStatement = ast match {
    case ForStatement0(x3, x5, x7, x9, params, span) =>
      ForStatement0(transform[Expression](x3, transform), transform[Expression](x5, transform), transform[Expression](x7, transform), transform(x9), params, span)
    case ForStatement1(x3, x5, x7, x9, params, span) =>
      ForStatement1(transform(x3), transform[Expression](x5, transform), transform[Expression](x7, transform), transform(x9), params, span)
    case ForStatement2(x2, x3, x5, x7, params, span) =>
      ForStatement2(transform(x2), transform[Expression](x3, transform), transform[Expression](x5, transform), transform(x7), params, span)
  }
  def transform(ast: ForInOfStatement): ForInOfStatement = ast match {
    case ForInOfStatement0(x3, x5, x7, params, span) =>
      ForInOfStatement0(transform(x3), transform(x5), transform(x7), params, span)
    case ForInOfStatement1(x3, x5, x7, params, span) =>
      ForInOfStatement1(transform(x3), transform(x5), transform(x7), params, span)
    case ForInOfStatement2(x2, x4, x6, params, span) =>
      ForInOfStatement2(transform(x2), transform(x4), transform(x6), params, span)
    case ForInOfStatement3(x3, x5, x7, params, span) =>
      ForInOfStatement3(transform(x3), transform(x5), transform(x7), params, span)
    case ForInOfStatement4(x3, x5, x7, params, span) =>
      ForInOfStatement4(transform(x3), transform(x5), transform(x7), params, span)
    case ForInOfStatement5(x2, x4, x6, params, span) =>
      ForInOfStatement5(transform(x2), transform(x4), transform(x6), params, span)
    case ForInOfStatement6(x4, x6, x8, params, span) =>
      ForInOfStatement6(transform(x4), transform(x6), transform(x8), params, span)
    case ForInOfStatement7(x4, x6, x8, params, span) =>
      ForInOfStatement7(transform(x4), transform(x6), transform(x8), params, span)
    case ForInOfStatement8(x3, x5, x7, params, span) =>
      ForInOfStatement8(transform(x3), transform(x5), transform(x7), params, span)
  }
  def transform(ast: ForDeclaration): ForDeclaration = ast match {
    case ForDeclaration0(x0, x1, params, span) =>
      ForDeclaration0(transform(x0), transform(x1), params, span)
  }
  def transform(ast: ForBinding): ForBinding = ast match {
    case ForBinding0(x0, params, span) =>
      ForBinding0(transform(x0), params, span)
    case ForBinding1(x0, params, span) =>
      ForBinding1(transform(x0), params, span)
  }
  def transform(ast: ContinueStatement): ContinueStatement = ast match {
    case ContinueStatement0(params, span) => ast
    case ContinueStatement1(x2, params, span) =>
      ContinueStatement1(transform(x2), params, span)
  }
  def transform(ast: BreakStatement): BreakStatement = ast match {
    case BreakStatement0(params, span) => ast
    case BreakStatement1(x2, params, span) =>
      BreakStatement1(transform(x2), params, span)
  }
  def transform(ast: ReturnStatement): ReturnStatement = ast match {
    case ReturnStatement0(params, span) => ast
    case ReturnStatement1(x2, params, span) =>
      ReturnStatement1(transform(x2), params, span)
  }
  def transform(ast: WithStatement): WithStatement = ast match {
    case WithStatement0(x2, x4, params, span) =>
      WithStatement0(transform(x2), transform(x4), params, span)
  }
  def transform(ast: SwitchStatement): SwitchStatement = ast match {
    case SwitchStatement0(x2, x4, params, span) =>
      SwitchStatement0(transform(x2), transform(x4), params, span)
  }
  def transform(ast: CaseBlock): CaseBlock = ast match {
    case CaseBlock0(x1, params, span) =>
      CaseBlock0(transform[CaseClauses](x1, transform), params, span)
    case CaseBlock1(x1, x2, x3, params, span) =>
      CaseBlock1(transform[CaseClauses](x1, transform), transform(x2), transform[CaseClauses](x3, transform), params, span)
  }
  def transform(ast: CaseClauses): CaseClauses = ast match {
    case CaseClauses0(x0, params, span) =>
      CaseClauses0(transform(x0), params, span)
    case CaseClauses1(x0, x1, params, span) =>
      CaseClauses1(transform(x0), transform(x1), params, span)
  }
  def transform(ast: CaseClause): CaseClause = ast match {
    case CaseClause0(x1, x3, params, span) =>
      CaseClause0(transform(x1), transform[StatementList](x3, transform), params, span)
  }
  def transform(ast: DefaultClause): DefaultClause = ast match {
    case DefaultClause0(x2, params, span) =>
      DefaultClause0(transform[StatementList](x2, transform), params, span)
  }
  def transform(ast: LabelledStatement): LabelledStatement = ast match {
    case LabelledStatement0(x0, x2, params, span) =>
      LabelledStatement0(transform(x0), transform(x2), params, span)
  }
  def transform(ast: LabelledItem): LabelledItem = ast match {
    case LabelledItem0(x0, params, span) =>
      LabelledItem0(transform(x0), params, span)
    case LabelledItem1(x0, params, span) =>
      LabelledItem1(transform(x0), params, span)
  }
  def transform(ast: ThrowStatement): ThrowStatement = ast match {
    case ThrowStatement0(x2, params, span) =>
      ThrowStatement0(transform(x2), params, span)
  }
  def transform(ast: TryStatement): TryStatement = ast match {
    case TryStatement0(x1, x2, params, span) =>
      TryStatement0(transform(x1), transform(x2), params, span)
    case TryStatement1(x1, x2, params, span) =>
      TryStatement1(transform(x1), transform(x2), params, span)
    case TryStatement2(x1, x2, x3, params, span) =>
      TryStatement2(transform(x1), transform(x2), transform(x3), params, span)
  }
  def transform(ast: Catch): Catch = ast match {
    case Catch0(x2, x4, params, span) =>
      Catch0(transform(x2), transform(x4), params, span)
    case Catch1(x1, params, span) =>
      Catch1(transform(x1), params, span)
  }
  def transform(ast: Finally): Finally = ast match {
    case Finally0(x1, params, span) =>
      Finally0(transform(x1), params, span)
  }
  def transform(ast: CatchParameter): CatchParameter = ast match {
    case CatchParameter0(x0, params, span) =>
      CatchParameter0(transform(x0), params, span)
    case CatchParameter1(x0, params, span) =>
      CatchParameter1(transform(x0), params, span)
  }
  def transform(ast: DebuggerStatement): DebuggerStatement = ast match {
    case DebuggerStatement0(params, span) => ast
  }
  def transform(ast: UniqueFormalParameters): UniqueFormalParameters = ast match {
    case UniqueFormalParameters0(x0, params, span) =>
      UniqueFormalParameters0(transform(x0), params, span)
  }
  def transform(ast: FormalParameters): FormalParameters = ast match {
    case FormalParameters0(params, span) => ast
    case FormalParameters1(x0, params, span) =>
      FormalParameters1(transform(x0), params, span)
    case FormalParameters2(x0, params, span) =>
      FormalParameters2(transform(x0), params, span)
    case FormalParameters3(x0, params, span) =>
      FormalParameters3(transform(x0), params, span)
    case FormalParameters4(x0, x2, params, span) =>
      FormalParameters4(transform(x0), transform(x2), params, span)
  }
  def transform(ast: FormalParameterList): FormalParameterList = ast match {
    case FormalParameterList0(x0, params, span) =>
      FormalParameterList0(transform(x0), params, span)
    case FormalParameterList1(x0, x2, params, span) =>
      FormalParameterList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: FunctionRestParameter): FunctionRestParameter = ast match {
    case FunctionRestParameter0(x0, params, span) =>
      FunctionRestParameter0(transform(x0), params, span)
  }
  def transform(ast: FormalParameter): FormalParameter = ast match {
    case FormalParameter0(x0, params, span) =>
      FormalParameter0(transform(x0), params, span)
  }
  def transform(ast: FunctionDeclaration): FunctionDeclaration = ast match {
    case FunctionDeclaration0(x1, x3, x6, params, span) =>
      FunctionDeclaration0(transform(x1), transform(x3), transform(x6), params, span)
    case FunctionDeclaration1(x2, x5, params, span) =>
      FunctionDeclaration1(transform(x2), transform(x5), params, span)
  }
  def transform(ast: FunctionExpression): FunctionExpression = ast match {
    case FunctionExpression0(x1, x3, x6, params, span) =>
      FunctionExpression0(transform[BindingIdentifier](x1, transform), transform(x3), transform(x6), params, span)
  }
  def transform(ast: FunctionBody): FunctionBody = ast match {
    case FunctionBody0(x0, params, span) =>
      FunctionBody0(transform(x0), params, span)
  }
  def transform(ast: FunctionStatementList): FunctionStatementList = ast match {
    case FunctionStatementList0(x0, params, span) =>
      FunctionStatementList0(transform[StatementList](x0, transform), params, span)
  }
  def transform(ast: ArrowFunction): ArrowFunction = ast match {
    case ArrowFunction0(x0, x3, params, span) =>
      ArrowFunction0(transform(x0), transform(x3), params, span)
  }
  def transform(ast: ArrowParameters): ArrowParameters = ast match {
    case ArrowParameters0(x0, params, span) =>
      ArrowParameters0(transform(x0), params, span)
    case ArrowParameters1(x0, params, span) =>
      ArrowParameters1(transform(x0), params, span)
  }
  def transform(ast: ConciseBody): ConciseBody = ast match {
    case ConciseBody0(x1, params, span) =>
      ConciseBody0(transform(x1), params, span)
    case ConciseBody1(x1, params, span) =>
      ConciseBody1(transform(x1), params, span)
  }
  def transform(ast: ExpressionBody): ExpressionBody = ast match {
    case ExpressionBody0(x0, params, span) =>
      ExpressionBody0(transform(x0), params, span)
  }
  def transform(ast: ArrowFormalParameters): ArrowFormalParameters = ast match {
    case ArrowFormalParameters0(x1, params, span) =>
      ArrowFormalParameters0(transform(x1), params, span)
  }
  def transform(ast: MethodDefinition): MethodDefinition = ast match {
    case MethodDefinition0(x0, x2, x5, params, span) =>
      MethodDefinition0(transform(x0), transform(x2), transform(x5), params, span)
    case MethodDefinition1(x0, params, span) =>
      MethodDefinition1(transform(x0), params, span)
    case MethodDefinition2(x0, params, span) =>
      MethodDefinition2(transform(x0), params, span)
    case MethodDefinition3(x0, params, span) =>
      MethodDefinition3(transform(x0), params, span)
    case MethodDefinition4(x1, x5, params, span) =>
      MethodDefinition4(transform(x1), transform(x5), params, span)
    case MethodDefinition5(x1, x3, x6, params, span) =>
      MethodDefinition5(transform(x1), transform(x3), transform(x6), params, span)
  }
  def transform(ast: PropertySetParameterList): PropertySetParameterList = ast match {
    case PropertySetParameterList0(x0, params, span) =>
      PropertySetParameterList0(transform(x0), params, span)
  }
  def transform(ast: GeneratorMethod): GeneratorMethod = ast match {
    case GeneratorMethod0(x1, x3, x6, params, span) =>
      GeneratorMethod0(transform(x1), transform(x3), transform(x6), params, span)
  }
  def transform(ast: GeneratorDeclaration): GeneratorDeclaration = ast match {
    case GeneratorDeclaration0(x2, x4, x7, params, span) =>
      GeneratorDeclaration0(transform(x2), transform(x4), transform(x7), params, span)
    case GeneratorDeclaration1(x3, x6, params, span) =>
      GeneratorDeclaration1(transform(x3), transform(x6), params, span)
  }
  def transform(ast: GeneratorExpression): GeneratorExpression = ast match {
    case GeneratorExpression0(x2, x4, x7, params, span) =>
      GeneratorExpression0(transform[BindingIdentifier](x2, transform), transform(x4), transform(x7), params, span)
  }
  def transform(ast: GeneratorBody): GeneratorBody = ast match {
    case GeneratorBody0(x0, params, span) =>
      GeneratorBody0(transform(x0), params, span)
  }
  def transform(ast: YieldExpression): YieldExpression = ast match {
    case YieldExpression0(params, span) => ast
    case YieldExpression1(x2, params, span) =>
      YieldExpression1(transform(x2), params, span)
    case YieldExpression2(x3, params, span) =>
      YieldExpression2(transform(x3), params, span)
  }
  def transform(ast: AsyncGeneratorMethod): AsyncGeneratorMethod = ast match {
    case AsyncGeneratorMethod0(x3, x5, x8, params, span) =>
      AsyncGeneratorMethod0(transform(x3), transform(x5), transform(x8), params, span)
  }
  def transform(ast: AsyncGeneratorDeclaration): AsyncGeneratorDeclaration = ast match {
    case AsyncGeneratorDeclaration0(x4, x6, x9, params, span) =>
      AsyncGeneratorDeclaration0(transform(x4), transform(x6), transform(x9), params, span)
    case AsyncGeneratorDeclaration1(x5, x8, params, span) =>
      AsyncGeneratorDeclaration1(transform(x5), transform(x8), params, span)
  }
  def transform(ast: AsyncGeneratorExpression): AsyncGeneratorExpression = ast match {
    case AsyncGeneratorExpression0(x4, x6, x9, params, span) =>
      AsyncGeneratorExpression0(transform[BindingIdentifier](x4, transform), transform(x6), transform(x9), params, span)
  }
  def transform(ast: AsyncGeneratorBody): AsyncGeneratorBody = ast match {
    case AsyncGeneratorBody0(x0, params, span) =>
      AsyncGeneratorBody0(transform(x0), params, span)
  }
  def transform(ast: ClassDeclaration): ClassDeclaration = ast match {
    case ClassDeclaration0(x1, x2, params, span) =>
      ClassDeclaration0(transform(x1), transform(x2), params, span)
    case ClassDeclaration1(x1, params, span) =>
      ClassDeclaration1(transform(x1), params, span)
  }
  def transform(ast: ClassExpression): ClassExpression = ast match {
    case ClassExpression0(x1, x2, params, span) =>
      ClassExpression0(transform[BindingIdentifier](x1, transform), transform(x2), params, span)
  }
  def transform(ast: ClassTail): ClassTail = ast match {
    case ClassTail0(x0, x2, params, span) =>
      ClassTail0(transform[ClassHeritage](x0, transform), transform[ClassBody](x2, transform), params, span)
  }
  def transform(ast: ClassHeritage): ClassHeritage = ast match {
    case ClassHeritage0(x1, params, span) =>
      ClassHeritage0(transform(x1), params, span)
  }
  def transform(ast: ClassBody): ClassBody = ast match {
    case ClassBody0(x0, params, span) =>
      ClassBody0(transform(x0), params, span)
  }
  def transform(ast: ClassElementList): ClassElementList = ast match {
    case ClassElementList0(x0, params, span) =>
      ClassElementList0(transform(x0), params, span)
    case ClassElementList1(x0, x1, params, span) =>
      ClassElementList1(transform(x0), transform(x1), params, span)
  }
  def transform(ast: ClassElement): ClassElement = ast match {
    case ClassElement0(x0, params, span) =>
      ClassElement0(transform(x0), params, span)
    case ClassElement1(x1, params, span) =>
      ClassElement1(transform(x1), params, span)
    case ClassElement2(params, span) => ast
  }
  def transform(ast: AsyncFunctionDeclaration): AsyncFunctionDeclaration = ast match {
    case AsyncFunctionDeclaration0(x3, x5, x8, params, span) =>
      AsyncFunctionDeclaration0(transform(x3), transform(x5), transform(x8), params, span)
    case AsyncFunctionDeclaration1(x4, x7, params, span) =>
      AsyncFunctionDeclaration1(transform(x4), transform(x7), params, span)
  }
  def transform(ast: AsyncFunctionExpression): AsyncFunctionExpression = ast match {
    case AsyncFunctionExpression0(x3, x5, x8, params, span) =>
      AsyncFunctionExpression0(transform[BindingIdentifier](x3, transform), transform(x5), transform(x8), params, span)
  }
  def transform(ast: AsyncMethod): AsyncMethod = ast match {
    case AsyncMethod0(x2, x4, x7, params, span) =>
      AsyncMethod0(transform(x2), transform(x4), transform(x7), params, span)
  }
  def transform(ast: AsyncFunctionBody): AsyncFunctionBody = ast match {
    case AsyncFunctionBody0(x0, params, span) =>
      AsyncFunctionBody0(transform(x0), params, span)
  }
  def transform(ast: AwaitExpression): AwaitExpression = ast match {
    case AwaitExpression0(x1, params, span) =>
      AwaitExpression0(transform(x1), params, span)
  }
  def transform(ast: AsyncArrowFunction): AsyncArrowFunction = ast match {
    case AsyncArrowFunction0(x2, x5, params, span) =>
      AsyncArrowFunction0(transform(x2), transform(x5), params, span)
    case AsyncArrowFunction1(x0, x3, params, span) =>
      AsyncArrowFunction1(transform(x0), transform(x3), params, span)
  }
  def transform(ast: AsyncConciseBody): AsyncConciseBody = ast match {
    case AsyncConciseBody0(x1, params, span) =>
      AsyncConciseBody0(transform(x1), params, span)
    case AsyncConciseBody1(x1, params, span) =>
      AsyncConciseBody1(transform(x1), params, span)
  }
  def transform(ast: AsyncArrowBindingIdentifier): AsyncArrowBindingIdentifier = ast match {
    case AsyncArrowBindingIdentifier0(x0, params, span) =>
      AsyncArrowBindingIdentifier0(transform(x0), params, span)
  }
  def transform(ast: CoverCallExpressionAndAsyncArrowHead): CoverCallExpressionAndAsyncArrowHead = ast match {
    case CoverCallExpressionAndAsyncArrowHead0(x0, x1, params, span) =>
      CoverCallExpressionAndAsyncArrowHead0(transform(x0), transform(x1), params, span)
  }
  def transform(ast: AsyncArrowHead): AsyncArrowHead = ast match {
    case AsyncArrowHead0(x2, params, span) =>
      AsyncArrowHead0(transform(x2), params, span)
  }
  def transform(ast: Script): Script = ast match {
    case Script0(x0, params, span) =>
      Script0(transform[ScriptBody](x0, transform), params, span)
  }
  def transform(ast: ScriptBody): ScriptBody = ast match {
    case ScriptBody0(x0, params, span) =>
      ScriptBody0(transform(x0), params, span)
  }
  def transform(ast: Module): Module = ast match {
    case Module0(x0, params, span) =>
      Module0(transform[ModuleBody](x0, transform), params, span)
  }
  def transform(ast: ModuleBody): ModuleBody = ast match {
    case ModuleBody0(x0, params, span) =>
      ModuleBody0(transform(x0), params, span)
  }
  def transform(ast: ModuleItemList): ModuleItemList = ast match {
    case ModuleItemList0(x0, params, span) =>
      ModuleItemList0(transform(x0), params, span)
    case ModuleItemList1(x0, x1, params, span) =>
      ModuleItemList1(transform(x0), transform(x1), params, span)
  }
  def transform(ast: ModuleItem): ModuleItem = ast match {
    case ModuleItem0(x0, params, span) =>
      ModuleItem0(transform(x0), params, span)
    case ModuleItem1(x0, params, span) =>
      ModuleItem1(transform(x0), params, span)
    case ModuleItem2(x0, params, span) =>
      ModuleItem2(transform(x0), params, span)
  }
  def transform(ast: ImportDeclaration): ImportDeclaration = ast match {
    case ImportDeclaration0(x1, x2, params, span) =>
      ImportDeclaration0(transform(x1), transform(x2), params, span)
    case ImportDeclaration1(x1, params, span) =>
      ImportDeclaration1(transform(x1), params, span)
  }
  def transform(ast: ImportClause): ImportClause = ast match {
    case ImportClause0(x0, params, span) =>
      ImportClause0(transform(x0), params, span)
    case ImportClause1(x0, params, span) =>
      ImportClause1(transform(x0), params, span)
    case ImportClause2(x0, params, span) =>
      ImportClause2(transform(x0), params, span)
    case ImportClause3(x0, x2, params, span) =>
      ImportClause3(transform(x0), transform(x2), params, span)
    case ImportClause4(x0, x2, params, span) =>
      ImportClause4(transform(x0), transform(x2), params, span)
  }
  def transform(ast: ImportedDefaultBinding): ImportedDefaultBinding = ast match {
    case ImportedDefaultBinding0(x0, params, span) =>
      ImportedDefaultBinding0(transform(x0), params, span)
  }
  def transform(ast: NameSpaceImport): NameSpaceImport = ast match {
    case NameSpaceImport0(x2, params, span) =>
      NameSpaceImport0(transform(x2), params, span)
  }
  def transform(ast: NamedImports): NamedImports = ast match {
    case NamedImports0(params, span) => ast
    case NamedImports1(x1, params, span) =>
      NamedImports1(transform(x1), params, span)
    case NamedImports2(x1, params, span) =>
      NamedImports2(transform(x1), params, span)
  }
  def transform(ast: FromClause): FromClause = ast match {
    case FromClause0(x1, params, span) =>
      FromClause0(transform(x1), params, span)
  }
  def transform(ast: ImportsList): ImportsList = ast match {
    case ImportsList0(x0, params, span) =>
      ImportsList0(transform(x0), params, span)
    case ImportsList1(x0, x2, params, span) =>
      ImportsList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: ImportSpecifier): ImportSpecifier = ast match {
    case ImportSpecifier0(x0, params, span) =>
      ImportSpecifier0(transform(x0), params, span)
    case ImportSpecifier1(x0, x2, params, span) =>
      ImportSpecifier1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: ModuleSpecifier): ModuleSpecifier = ast match {
    case ModuleSpecifier0(x0, params, span) =>
      ModuleSpecifier0(transform(x0), params, span)
  }
  def transform(ast: ImportedBinding): ImportedBinding = ast match {
    case ImportedBinding0(x0, params, span) =>
      ImportedBinding0(transform(x0), params, span)
  }
  def transform(ast: ExportDeclaration): ExportDeclaration = ast match {
    case ExportDeclaration0(x1, x2, params, span) =>
      ExportDeclaration0(transform(x1), transform(x2), params, span)
    case ExportDeclaration1(x1, params, span) =>
      ExportDeclaration1(transform(x1), params, span)
    case ExportDeclaration2(x1, params, span) =>
      ExportDeclaration2(transform(x1), params, span)
    case ExportDeclaration3(x1, params, span) =>
      ExportDeclaration3(transform(x1), params, span)
    case ExportDeclaration4(x2, params, span) =>
      ExportDeclaration4(transform(x2), params, span)
    case ExportDeclaration5(x2, params, span) =>
      ExportDeclaration5(transform(x2), params, span)
    case ExportDeclaration6(x3, params, span) =>
      ExportDeclaration6(transform(x3), params, span)
  }
  def transform(ast: ExportFromClause): ExportFromClause = ast match {
    case ExportFromClause0(params, span) => ast
    case ExportFromClause1(x2, params, span) =>
      ExportFromClause1(transform(x2), params, span)
    case ExportFromClause2(x0, params, span) =>
      ExportFromClause2(transform(x0), params, span)
  }
  def transform(ast: NamedExports): NamedExports = ast match {
    case NamedExports0(params, span) => ast
    case NamedExports1(x1, params, span) =>
      NamedExports1(transform(x1), params, span)
    case NamedExports2(x1, params, span) =>
      NamedExports2(transform(x1), params, span)
  }
  def transform(ast: ExportsList): ExportsList = ast match {
    case ExportsList0(x0, params, span) =>
      ExportsList0(transform(x0), params, span)
    case ExportsList1(x0, x2, params, span) =>
      ExportsList1(transform(x0), transform(x2), params, span)
  }
  def transform(ast: ExportSpecifier): ExportSpecifier = ast match {
    case ExportSpecifier0(x0, params, span) =>
      ExportSpecifier0(transform(x0), params, span)
    case ExportSpecifier1(x0, x2, params, span) =>
      ExportSpecifier1(transform(x0), transform(x2), params, span)
  }
}
