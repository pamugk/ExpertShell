package base.environmentvars;

//Confirmation factors for calculation of fuzzy variables combination way
public enum CFVA {
    PP, //probabilistic sum of products
    PM, //probabilistic sum of minimums
    PA, //probabilistic suu of averages
    PB, //probabilistic sum of balances
    AP, //average of multiplications
    AM, //average of minimums
    AB, //average of balances
    BP, //balance of products
    BM, //balance of minimums
    BA, //balance of averages
    BB //balance of balances
}
