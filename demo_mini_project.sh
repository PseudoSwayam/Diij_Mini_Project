#!/bin/bash
# Mini Project - Automated Demo with Clean Output
DERBY_JAR="/opt/homebrew/Cellar/derby/10.17.1.0/libexec/lib/derby.jar"
JAVA_CMD="/opt/homebrew/opt/openjdk/bin/java"
PROJECT_DIR="/Users/swayamsahoo/Projects/diij_lab/comm/dbms/mini_project"
cd "$PROJECT_DIR"
echo "========================================"
echo "Mini Project - Library Loan Management"
echo "========================================"
echo ""
echo "📚 Project Overview:"
echo "   - Library Member Registration"
echo "   - Book Catalog Management"
echo "   - Loan Processing with Transactions"
echo "   - Return Management"
echo "   - Query Active Loans"
echo "   - Performance Evaluation"
echo ""
# Clean previous database
rm -rf SwayamDB 2>/dev/null
# Quick demo without performance evaluation
echo "🚀 Initializing System..."
echo ""
$JAVA_CMD -cp "$DERBY_JAR:out" pkg2341019067.MainApp << 'EOF'
1
2
Alice
3
978-0134685991
Effective Java
3
978-0201633610
Design Patterns
4
1
1
4
2
1
6
1
8
EOF
