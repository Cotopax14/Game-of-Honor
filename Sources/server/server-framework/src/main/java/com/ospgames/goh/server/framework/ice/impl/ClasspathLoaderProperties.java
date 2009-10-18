package com.ospgames.goh.server.framework.ice.impl;

import Ice.*;

/**
 *
 * This is a clone of Ice.PropertiesI and adds only the loadFromStream method,
 * all other methods remain untouched.
 *
 * @author kempa
 * @version 30.04.2007
 */
public class ClasspathLoaderProperties implements Properties {

    /**
     * Uses the given input stream to read the properties from.
     * This can be used to read from classpath.
     *
     * @param in the input stream to read from, not null.
     * @throws java.io.IOException if the stream could not be read from or does not support the UTF-8 encoding.
     */
    public void loadFromStream(java.io.InputStream in) throws  java.io.IOException {

        java.io.InputStreamReader isr = new java.io.InputStreamReader(in, "UTF-8");
        java.io.BufferedReader br = new java.io.BufferedReader(isr);
        parse(br);

    }


    class PropertyValue
    {
        public PropertyValue(PropertyValue v)
        {
            value = v.value;
            used = v.used;
        }

        public PropertyValue(String v, boolean u)
        {
            value = v;
            used = u;
        }

        public String value;
        public boolean used;
    }

    public synchronized String
    getProperty(String key)
    {
        String result = null;
        PropertyValue pv = _properties.get(key);
        if(pv == null)
        {
            try
            {
                result = System.getProperty(key, "");
            }
            catch(java.lang.SecurityException ex)
            {
                result = "";
            }
        }
        else
        {
            pv.used = true;
            result = pv.value;
        }
        return result;
    }

    public synchronized String
    getPropertyWithDefault(String key, String value)
    {
        String result = null;
        PropertyValue pv = _properties.get(key);
        if(pv == null)
        {
            try
            {
                result = System.getProperty(key, value);
            }
            catch(java.lang.SecurityException ex)
            {
                result = value;
            }
        }
        else
        {
            pv.used = true;
            result = pv.value;
        }
        return result;
    }

    public int
    getPropertyAsInt(String key)
    {
        return getPropertyAsIntWithDefault(key, 0);
    }

    public synchronized int
    getPropertyAsIntWithDefault(String key, int value)
    {
        String result = null;
        PropertyValue pv = _properties.get(key);
        if(pv == null)
        {
            try
            {
                result = System.getProperty(key);
            }
            catch(java.lang.SecurityException ex)
            {
            }
        }
        else
        {
            pv.used = true;
            result = pv.value;
        }
        if(result == null)
        {
            return value;
        }

        try
        {
            return Integer.parseInt(result);
        }
        catch(NumberFormatException ex)
        {
            Ice.Util.getProcessLogger().warning("numeric property " + key +
                                                " set to non-numeric value, defaulting to " + value);
            return value;
        }
    }

    public String[]
    getPropertyAsList(String key)
    {
        return getPropertyAsListWithDefault(key, null);
    }

    public synchronized String[]
    getPropertyAsListWithDefault(String key, String[] value)
    {
        if(value == null)
        {
            value = new String[0];
        }

        String result = null;
        PropertyValue pv = _properties.get(key);
        if(pv == null)
        {
            try
            {
                result = System.getProperty(key);
            }
            catch(java.lang.SecurityException ex)
            {
            }
            if(result == null)
            {
                return value;
            }
        }
        else
        {
            pv.used = true;
            result = pv.value;
        }

        String[] arr = splitString(result, ", \t\r\n");
        if(arr == null)
        {
            Ice.Util.getProcessLogger().warning("mismatched quotes in property " + key
                                                + "'s value, returning default value");
            return value;
        }
        else
        {
            return arr;
        }
    }


    public synchronized java.util.Map<String, String>
    getPropertiesForPrefix(String prefix)
    {
        java.util.HashMap<String, String> result = new java.util.HashMap<String, String>();
        java.util.Iterator<java.util.Map.Entry<String, PropertyValue> > p = _properties.entrySet().iterator();
        while(p.hasNext())
        {
            java.util.Map.Entry<String, PropertyValue> entry = p.next();
            String key = entry.getKey();
            if(prefix.length() == 0 || key.startsWith(prefix))
            {
                PropertyValue pv = entry.getValue();
                pv.used = true;
                result.put(key, pv.value);
            }
        }
        return result;
    }

    public void
    setProperty(String key, String value)
    {
        //
        // Trim whitespace
        //
        if(key != null)
        {
            key = key.trim();
        }

        //
        // Check if the property is legal.
        //
        Logger logger = Ice.Util.getProcessLogger();
        if(key == null || key.length() == 0)
        {
            throw new Ice.InitializationException("Attempt to set property with empty key");
        }

        int dotPos = key.indexOf('.');
        if(dotPos != -1)
        {
            String prefix = key.substring(0, dotPos);
            for(int i = 0; IceInternal.PropertyNames.validProps[i] != null; ++i)
            {
                String pattern = IceInternal.PropertyNames.validProps[i][0].pattern();
                dotPos = pattern.indexOf('.');
                //
                // Each top level prefix describes a non-empty namespace. Having a string without a
                // prefix followed by a dot is an error.
                //
                assert(dotPos != -1);
                String propPrefix = pattern.substring(0, dotPos - 1);
                if(!propPrefix.equals(prefix))
                {
                    continue;
                }

                boolean found = false;
                for(int j = 0; IceInternal.PropertyNames.validProps[i][j] != null && !found; ++j)
                {
                    pattern = IceInternal.PropertyNames.validProps[i][j].pattern();
                    java.util.regex.Pattern pComp = java.util.regex.Pattern.compile(pattern);
                    java.util.regex.Matcher m = pComp.matcher(key);
                    found = m.matches();

                    if(found && IceInternal.PropertyNames.validProps[i][j].deprecated())
                    {
                        logger.warning("deprecated property: " + key);
                        if(IceInternal.PropertyNames.validProps[i][j].deprecatedBy() != null)
                        {
                            key = IceInternal.PropertyNames.validProps[i][j].deprecatedBy();
                        }
                    }
                }
                if(!found)
                {
                    logger.warning("unknown property: " + key);
                }
            }
        }

        synchronized(this)
        {
            //
            // Set or clear the property.
            //
            if(value != null && value.length() > 0)
            {
                PropertyValue pv = _properties.get(key);
                if(pv != null)
                {
                    pv.value = value;
                }
                else
                {
                    pv = new PropertyValue(value, false);
                }
                _properties.put(key, pv);
            }
            else
            {
                _properties.remove(key);
            }
        }
    }

    public synchronized String[]
    getCommandLineOptions()
    {
        String[] result = new String[_properties.size()];
        java.util.Iterator<java.util.Map.Entry<String, PropertyValue> > p = _properties.entrySet().iterator();
        int i = 0;
        while(p.hasNext())
        {
            java.util.Map.Entry<String, PropertyValue> entry = p.next();
            result[i++] = "--" + entry.getKey() + "=" + entry.getValue().value;
        }
        assert(i == result.length);
        return result;
    }

    public String[]
    parseCommandLineOptions(String pfx, String[] options)
    {
        if(pfx.length() > 0 && pfx.charAt(pfx.length() - 1) != '.')
        {
            pfx += '.';
        }
        pfx = "--" + pfx;

        java.util.ArrayList<String> result = new java.util.ArrayList<String>();
        for(int i = 0; i < options.length; i++)
        {
            String opt = options[i];
            if(opt.startsWith(pfx))
            {
                if(opt.indexOf('=') == -1)
                {
                    opt += "=1";
                }

                parseLine(opt.substring(2));
            }
            else
            {
                result.add(opt);
            }
        }
        return (String[])result.toArray(new String[0]);
    }

    public String[]
    parseIceCommandLineOptions(String[] options)
    {
        String[] args = options;
        for(int i = 0; IceInternal.PropertyNames.clPropNames[i] != null; ++i)
        {
            args = parseCommandLineOptions(IceInternal.PropertyNames.clPropNames[i], args);
        }
        return args;
    }

    public void
    load(String file)
    {
        try
        {
            java.io.FileInputStream fis = new java.io.FileInputStream(file);
            java.io.InputStreamReader isr = new java.io.InputStreamReader(fis, "UTF-8");
            java.io.BufferedReader br = new java.io.BufferedReader(isr);
            parse(br);
        }
        catch(java.io.IOException ex)
        {
            FileException fe = new FileException();
            fe.path = file;
            fe.initCause(ex); // Exception chaining
            throw fe;
        }
    }

    public synchronized Properties
    _clone()
    {
        return new ClasspathLoaderProperties(this);
    }

    public synchronized java.util.List<String>
    getUnusedProperties()
    {
        java.util.List<String> unused = new java.util.ArrayList<String>();
        java.util.Iterator<java.util.Map.Entry<String, PropertyValue> > p = _properties.entrySet().iterator();
        while(p.hasNext())
        {
            java.util.Map.Entry<String, PropertyValue> entry = p.next();
            PropertyValue pv = entry.getValue();
            if(!pv.used)
            {
                unused.add(entry.getKey());
            }
        }
        return unused;
    }

    ClasspathLoaderProperties(ClasspathLoaderProperties props)
    {
        //
        // NOTE: we can't just do a shallow copy of the map as the map values
        // would otherwise be shared between the two PropertiesI object.
        //
        //_properties = new java.util.HashMap<String, PropertyValue>(props._properties);
        java.util.Iterator<java.util.Map.Entry<String, PropertyValue> > p = props._properties.entrySet().iterator();
        while(p.hasNext())
        {
            java.util.Map.Entry<String, PropertyValue> entry = p.next();
            _properties.put(entry.getKey(), new PropertyValue(entry.getValue()));
        }
    }

    public ClasspathLoaderProperties()
    {
    }

    ClasspathLoaderProperties(StringSeqHolder args, Properties defaults)
    {
        if(defaults != null)
        {
            _properties = new java.util.HashMap<String, PropertyValue>(((ClasspathLoaderProperties)defaults)._properties);
        }

        boolean loadConfigFiles = false;

        for(int i = 0; i < args.value.length; i++)
        {
            if(args.value[i].startsWith("--Ice.Config"))
            {
                String line = args.value[i];
                if(line.indexOf('=') == -1)
                {
                    line += "=1";
                }
                parseLine(line.substring(2));
                loadConfigFiles = true;

                String[] arr = new String[args.value.length - 1];
                System.arraycopy(args.value, 0, arr, 0, i);
                if(i < args.value.length - 1)
                {
                    System.arraycopy(args.value, i + 1, arr, i, args.value.length - i - 1);
                }
                args.value = arr;
            }
        }

        if(!loadConfigFiles)
        {
            //
            // If Ice.Config is not set, load from ICE_CONFIG (if set)
            //
            loadConfigFiles = !_properties.containsKey("Ice.Config");
        }

        if(loadConfigFiles)
        {
            loadConfig();
        }

        args.value = parseIceCommandLineOptions(args.value);
    }

    private void
    parse(java.io.BufferedReader in)
    {
        try
        {
            String line;
            while((line = in.readLine()) != null)
            {
                parseLine(line);
            }
        }
        catch(java.io.IOException ex)
        {
            SyscallException se = new SyscallException();
            se.initCause(ex); // Exception chaining
            throw se;
        }
    }

    private static final int ParseStateKey = 0;
    private static final int ParseStateValue = 1;

    private void
    parseLine(String line)
    {
        String key = "";
        String value = "";

        int state = ParseStateKey;

        String whitespace = "";
        String escapedspace = "";
        boolean finished = false;
        for(int i = 0; i < line.length(); ++i)
        {
            char c = line.charAt(i);
            switch(state)
            {
              case ParseStateKey:
              {
                  switch(c)
                  {
                    case '\\':
                      if(i < line.length() - 1)
                      {
                          c = line.charAt(++i);
                          switch(c)
                          {
                            case '\\':
                            case '#':
                            case '=':
                              key += whitespace;
                              whitespace = "";
                              key += c;
                              break;

                            case ' ':
                              if(key.length() != 0)
                              {
                                  whitespace += c;
                              }
                              break;

                            default:
                              key += whitespace;
                              whitespace = "";
                              key += '\\';
                              key += c;
                              break;
                          }
                      }
                      else
                      {
                          key += whitespace;
                          key += c;
                      }
                      break;

                    case ' ':
                    case '\t':
                    case '\r':
                    case '\n':
                        if(key.length() != 0)
                        {
                            whitespace += c;
                        }
                        break;

                    case '=':
                        whitespace = "";
                        state = ParseStateValue;
                        break;

                    case '#':
                        finished = true;
                        break;

                    default:
                        key += whitespace;
                        whitespace = "";
                        key += c;
                        break;
                  }
                  break;
              }

              case ParseStateValue:
              {
                  switch(c)
                  {
                    case '\\':
                      if(i < line.length() - 1)
                      {
                          c = line.charAt(++i);
                          switch(c)
                          {
                            case '\\':
                            case '#':
                            case '=':
                              value += value.length() == 0 ? escapedspace : whitespace;
                              whitespace = "";
                              escapedspace = "";
                              value += c;
                              break;

                            case ' ':
                              whitespace += c;
                              escapedspace += c;
                              break;

                            default:
                              value += value.length() == 0 ? escapedspace : whitespace;
                              whitespace = "";
                              escapedspace = "";
                              value += '\\';
                              value += c;
                              break;
                          }
                      }
                      else
                      {
                          value += value.length() == 0 ? escapedspace : whitespace;
                          value += c;
                      }
                      break;

                    case ' ':
                    case '\t':
                    case '\r':
                    case '\n':
                        if(value.length() != 0)
                        {
                            whitespace += c;
                        }
                        break;

                    case '#':
                        value += escapedspace;
                        finished = true;
                        break;

                    default:
                        value += value.length() == 0 ? escapedspace : whitespace;
                        whitespace = "";
                        escapedspace = "";
                        value += c;
                        break;
                  }
                  break;
              }
            }
            if(finished)
            {
                break;
            }
        }
        value += escapedspace;

        if((state == ParseStateKey && key.length() != 0) || (state == ParseStateValue && key.length() == 0))
        {
            Ice.Util.getProcessLogger().warning("invalid config file entry: \"" + line + "\"");
            return;
        }
        else if(key.length() == 0)
        {
            return;
        }

        setProperty(key, value);
    }

    private void
    loadConfig()
    {
        String value = getProperty("Ice.Config");

        if(value.length() == 0 || value.equals("1"))
        {
            try
            {
                value = System.getenv("ICE_CONFIG");
                if(value == null)
                {
                    value = "";
                }
            }
            catch(java.lang.SecurityException ex)
            {
                value = "";
            }
        }

        if(value.length() > 0)
        {
            String[] files = value.split(",");
            for(int i = 0; i < files.length; i++)
            {
                load(files[i]);
            }
        }

        _properties.put("Ice.Config", new PropertyValue(value, true));
    }

    //
    // Split string helper; returns null for unmatched quotes
    //
    private String[] splitString(String str, String delim)
    {
        java.util.List<String> l = new java.util.ArrayList<String>();
        char[] arr = new char[str.length()];
        int pos = 0;

        while(pos < str.length())
        {
            int n = 0;
            char quoteChar = '\0';
            if(str.charAt(pos) == '"' || str.charAt(pos) == '\'')
            {
                quoteChar = str.charAt(pos);
                ++pos;
            }
            while(pos < str.length())
            {
                if(quoteChar != '\0' && str.charAt(pos) == '\\' && pos + 1 < str.length() &&
                   str.charAt(pos + 1) == quoteChar)
                {
                    ++pos;
                }
                else if(quoteChar != '\0' && str.charAt(pos) == quoteChar)
                {
                    ++pos;
                    quoteChar = '\0';
                    break;
                }
                else if(delim.indexOf(str.charAt(pos)) != -1)
                {
                    if(quoteChar == '\0')
                    {
                        ++pos;
                        break;
                    }
                }

                if(pos < str.length())
                {
                    arr[n++] = str.charAt(pos++);
                }
            }
            if(quoteChar != '\0')
            {
                return null; // Unmatched quote.
            }
            if(n > 0)
            {
                l.add(new String(arr, 0, n));
            }
        }

        return (String[])l.toArray(new String[0]);
    }

    private java.util.HashMap<String, PropertyValue> _properties = new java.util.HashMap<String, PropertyValue>();
}
