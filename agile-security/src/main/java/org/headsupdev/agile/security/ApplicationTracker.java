/*
 * HeadsUp Agile
 * Copyright 2009-2012 Heads Up Development Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.headsupdev.agile.security;

import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.*;

import java.util.Set;
import java.util.HashSet;

import org.headsupdev.agile.api.Application;
import org.headsupdev.agile.api.Manager;

/**
 * A tracker to keep track of application services
 *
 * @author Andrew Williams
 * @since 1.0
 */
public class ApplicationTracker
    extends ServiceTracker
{
    private static Set<Bundle> bundles = new HashSet<Bundle>();
    private BundleContext bc;

    public ApplicationTracker( BundleContext bundleContext )
    {
        super( bundleContext, createFilter( bundleContext ), null );
        this.bc = bundleContext;
    }

    private static Filter createFilter( final BundleContext bundleContext )
    {
        final String filter = "(objectClass=org.headsupdev.agile.api.Application)";
        try
        {
            return bundleContext.createFilter( filter );
        }
        catch( InvalidSyntaxException e )
        {
            throw new IllegalArgumentException( "Unexpected InvalidSyntaxException: " + e.getMessage() );
        }
    }

    @Override
    public Object addingService( ServiceReference serviceReference )
    {
        Application app = (Application) bc.getService( serviceReference );
        ( (DefaultSecurityManager) Manager.getSecurityInstance() ).scanPermissions( app );

        return super.addingService( serviceReference );
    }

    @Override
    public void modifiedService( ServiceReference serviceReference, Object o )
    {
        Application app = (Application) o;
//        ( (DefaultSecurityManager) Manager.getSecurityInstance() ).removePermissions( app );
        ( (DefaultSecurityManager) Manager.getSecurityInstance() ).scanPermissions( app );

        super.modifiedService( serviceReference, o );
    }

    @Override
    public void removedService( ServiceReference serviceReference, Object o )
    {
        Application app = (Application) o;
//        ( (DefaultSecurityManager) Manager.getSecurityInstance() ).removePermissions( app );

        super.removedService( serviceReference, o );
    }

    public static Set<Bundle> getBundles()
    {
        return bundles;
    }
}
