package org.acme.insurance.launcher;

import java.io.InputStream;

import org.acme.insurance.Driver;
import org.acme.insurance.Policy;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * This is a sample file to launch a rule package from a rule source file.
 */
public class PricingRuleLauncher {

    public static final void main(String[] args) throws Exception {
        PricingRuleLauncher launcher = new PricingRuleLauncher();
        launcher.executeExample();
    }

    public int executeExample() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType( DecisionTableInputType.XLS );

        kbuilder.add( ResourceFactory.newClassPathResource( "/data/ExamplePolicyPricing.xls", getClass() ),
                              ResourceType.DTABLE,
                              dtconf );

        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( kbuilder.getErrors().toString() );
        }

        //BUILD RULEBASE
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        //NEW WORKING MEMORY
        final StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        //now create some test data
        Driver driver = new Driver();
        Policy policy = new Policy();

        session.insert( driver );
        session.insert( policy );

        session.fireAllRules();

        System.out.println( "BASE PRICE IS: " + policy.getBasePrice() );
        System.out.println( "DISCOUNT IS: " + policy.getDiscountPercent() );

        return policy.getBasePrice();

    }

    private InputStream getSpreadsheetStream() {
        return this.getClass().getResourceAsStream( "/data/ExamplePolicyPricing.xls" );
    }

}
