# KawachYatri - Deployment Guide

## 🚀 Deploy to Render (Free Permanent Link)

Follow these steps to get your permanent link like `https://kawachyatri.onrender.com`:

### Step 1: Push Your Code to GitHub

If you haven't already pushed your code:

```bash
git add .
git commit -m "Prepare for deployment"
git push origin main
```

### Step 2: Sign Up on Render

1. Go to [https://render.com](https://render.com)
2. Click **"Get Started"** or **"Sign Up"**
3. Sign up with your **GitHub account** (recommended)
4. Authorize Render to access your repositories

### Step 3: Create New Web Service

1. Click **"New +"** button (top right)
2. Select **"Web Service"**
3. Connect your GitHub repository:
   - Search for `teamnova`
   - Click **"Connect"**

### Step 4: Configure Your Service

Fill in the deployment settings:

- **Name**: `kawachyatri` (or any name you prefer)
- **Region**: Choose closest to you
- **Branch**: `main`
- **Root Directory**: Leave empty
- **Environment**: `Docker`
- **Plan**: Select **"Free"** (0$/month)

### Step 5: Deploy

1. Click **"Create Web Service"**
2. Render will automatically:
   - Build your Docker image
   - Compile your Java code
   - Deploy your application
3. Wait 2-5 minutes for first deployment

### Step 6: Get Your Permanent Link

Once deployed, you'll see:

```
✅ Live at: https://kawachyatri.onrender.com
```

**Your app is now live permanently!**

---

## 🌐 Your Permanent URLs

After deployment, access your app at:

- **Main App**: `https://kawachyatri.onrender.com`
- **Login**: `https://kawachyatri.onrender.com/login`
- **Home**: `https://kawachyatri.onrender.com/home`
- **Trip Setup**: `https://kawachyatri.onrender.com/trip-setup`
- **API**: `https://kawachyatri.onrender.com/api/indices?city=Mumbai`

Replace `kawachyatri` with whatever name you chose.

---

## ⚙️ Important Notes

### Free Tier Limitations:
- ✅ **Unlimited traffic** (no bandwidth limits)
- ✅ **Always online** (automatic restarts)
- ⚠️ **Spins down after 15 min of inactivity** (first request may take 30-50 seconds to wake up)
- ✅ **Auto-deploys** when you push to GitHub

### To Keep It Always Active (Optional):
1. Upgrade to paid plan ($7/month)
2. Or use a free uptime monitor like [UptimeRobot](https://uptimerobot.com) to ping your site every 5 minutes

---

## 🔄 Update Your Deployed App

Whenever you make changes:

```bash
git add .
git commit -m "Your update message"
git push origin main
```

Render will automatically:
1. Detect the changes
2. Rebuild and redeploy
3. Your app will be updated in ~2-3 minutes

---

## 📱 Share Your App

Once deployed, share your link:
- **Friends/Family**: `https://kawachyatri.onrender.com`
- **Hackathon judges**: Send them the link
- **Portfolio**: Add to your resume/portfolio

---

## 🐛 Troubleshooting

### Build Failed?
Check the Render logs for errors. Common fixes:
- Ensure `backend.java` compiles locally
- Ensure `Dockerfile` is in the root directory

### App Not Loading?
- First load after inactivity takes 30-50 seconds (free tier)
- Check if PORT environment variable is set correctly

### Need Help?
Check Render documentation: https://render.com/docs

---

## 🎉 Next Steps

1. Deploy to Render
2. Get your permanent link
3. Test all features
4. Share with your team/judges
5. Add custom domain (optional): Go to Render dashboard → Custom Domain

**Good luck with your deployment! 🚀**
